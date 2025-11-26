package com.vlad.todo.service;

import static com.vlad.todo.service.UserService.USER_WITH_ID_NOT_FOUND;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.exception.InvalidInputException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.mapper.TaskMapper;
import com.vlad.todo.model.Task;
import com.vlad.todo.model.User;
import com.vlad.todo.repository.TaskRepository;
import com.vlad.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * TaskService — сервис управления задачами с проверкой доступа по ролям.
 *
 * Правила:
 * - ADMIN: может смотреть/создавать/изменять/удалять любые задачи.
 * - USER: может смотреть/создавать/изменять/удалять только свои задачи.
 */
@Service
@AllArgsConstructor
@Transactional
public class TaskService {
    public static final String TASK_WITH_ID_NOT_FOUND = "Задача с id %d не найдена";

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /* ---------------------- Вспомогательные методы для доступа ---------------------- */

    private Authentication getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new InvalidInputException("Пользователь не аутентифицирован");
        }
        return auth;
    }

    private String getCurrentUsername() {
        return getAuth().getName();
    }

    private boolean isAdmin() {
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private User getCurrentUser() {
        String email = getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(USER_WITH_ID_NOT_FOUND, userId));
        }
    }

    /* ---------------------- API методы (совместимы с исходным контроллером) ---------------------- */

    /**
     * Возвращает задачи.
     * - ADMIN: все задачи.
     * - USER: только свои задачи.
     */
    public List<TaskDtoResponse> findAllTasks() {
        List<Task> tasks;
        if (isAdmin()) {
            tasks = taskRepository.findAll();
        } else {
            long userId = getCurrentUser().getId();
            tasks = taskRepository.findByUserId(userId);
        }

        List<TaskDtoResponse> dto = new ArrayList<>();
        tasks.forEach(t -> dto.add(taskMapper.toDto(t)));
        return dto;
    }

    /**
     * Возвращает задачи пользователя по userId.
     * - ADMIN: любой userId.
     * - USER: только если userId == текущий пользователь.
     */
    public List<TaskDtoResponse> findTasksByUser(long userId) {
        if (userId < 1) throw new InvalidInputException("Id пользователя должен быть больше 0");

        if (!isAdmin()) {
            long currentId = getCurrentUser().getId();
            if (currentId != userId) {
                throw new InvalidInputException("Вы не можете просматривать задачи других пользователей");
            }
        }

        List<Task> tasks = taskRepository.findByUserId(userId);
        List<TaskDtoResponse> dto = new ArrayList<>();
        tasks.forEach(t -> dto.add(taskMapper.toDto(t)));
        return dto;
    }

    /**
     * Возвращает задачу по id с проверкой доступа.
     * - ADMIN: любую задачу.
     * - USER: только свою.
     */
    public TaskDtoResponse findTaskById(long id) {
        if (id < 1) throw new InvalidInputException("Id должен быть больше 0");

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TASK_WITH_ID_NOT_FOUND, id)));

        if (!isAdmin() && task.getUser() != null && task.getUser().getId() != getCurrentUser().getId()) {
            throw new InvalidInputException("Вы не можете просматривать эту задачу");
        }

        return taskMapper.toDto(task);
    }

    /**
     * Создать задачу.
     * - USER: задача привязывается к текущему пользователю (userId в DTO игнорируется).
     * - ADMIN: должен передать userId в DTO — сохраняем задачу указанному пользователю.
     */
    public TaskDtoResponse saveTask(TaskDtoRequest taskDtoRequest) {
        if (isAdmin()) {
            // ADMIN должен указать корректный userId
            if (taskDtoRequest.getUserId() == null || taskDtoRequest.getUserId() < 1) {
                throw new InvalidInputException("Для ADMIN нужно указать userId");
            }
            checkUserExists(taskDtoRequest.getUserId());
            User user = userRepository.findById(taskDtoRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException(String.format(USER_WITH_ID_NOT_FOUND, taskDtoRequest.getUserId())));

            Task task = taskMapper.toEntity(taskDtoRequest);
            task.setUser(user);
            taskRepository.save(task);
            return taskMapper.toDto(task);

        } else {
            // USER: привязать к текущему пользователю
            User current = getCurrentUser();
            Task task = taskMapper.toEntity(taskDtoRequest);
            task.setUser(current);
            taskRepository.save(task);
            return taskMapper.toDto(task);
        }
    }

    /**
     * Обновить задачу (с проверкой прав).
     * - USER: может обновлять только свои.
     * - ADMIN: любую.
     */
    public TaskDtoResponse updateTask(long id, TaskDtoRequest taskDtoRequest) {
        if (id < 1) throw new InvalidInputException("Id должен быть больше 0");

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TASK_WITH_ID_NOT_FOUND, id)));

        if (!isAdmin() && task.getUser() != null && task.getUser().getId() != getCurrentUser().getId()) {
            throw new InvalidInputException("Вы не можете изменять эту задачу");
        }

        // частичное обновление полей
        if (taskDtoRequest.getTitle() != null) task.setTitle(taskDtoRequest.getTitle());
        if (taskDtoRequest.getContent() != null) task.setContent(taskDtoRequest.getContent());
        if (taskDtoRequest.getIsCompleted() != null) task.setIsCompleted(taskDtoRequest.getIsCompleted());
        if (taskDtoRequest.getDeadlineDate() != null) task.setDeadlineDate(taskDtoRequest.getDeadlineDate());
        if (taskDtoRequest.getIsImportant() != null) task.setIsImportant(taskDtoRequest.getIsImportant());

        // ADMIN может поменять владельца — если указан userId и это валидно
        if (isAdmin() && taskDtoRequest.getUserId() != null && taskDtoRequest.getUserId() > 0) {
            User newOwner = userRepository.findById(taskDtoRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException(String.format(USER_WITH_ID_NOT_FOUND, taskDtoRequest.getUserId())));
            task.setUser(newOwner);
        }

        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * Удалить задачу (с проверкой прав).
     * - USER: только свою.
     * - ADMIN: любую.
     */
    public void deleteTaskById(long id) {
        if (id < 1) throw new InvalidInputException("Id должен быть больше 0");

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TASK_WITH_ID_NOT_FOUND, id)));

        if (!isAdmin() && task.getUser() != null && task.getUser().getId() != getCurrentUser().getId()) {
            throw new InvalidInputException("Вы не можете удалять эту задачу");
        }

        taskRepository.delete(task);
    }

    /* ---------------------- Доп. удобные методы (если где-то используются) ---------------------- */

    /**
     * Удобный метод для контроллера `/tasks/my` — возвращает задачи текущего пользователя.
     */
    public List<TaskDtoResponse> findTasksForCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));
        List<Task> tasks = taskRepository.findByUserId(user.getId());
        List<TaskDtoResponse> dto = new ArrayList<>();
        tasks.forEach(t -> dto.add(taskMapper.toDto(t)));
        return dto;
    }

    /**
     * Метод с явной сигнатурой "withAccess" для контроллерей, которые ожидали такую сигнатуру.
     * Делегирует на базовые методы и выполняет те же проверки.
     */
    public List<TaskDtoResponse> findTasksByUserWithAccess(long userId, String currentUserEmail) {
        User current = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + currentUserEmail));

        if (!isAdmin() && current.getId() != userId) {
            throw new InvalidInputException("Вы не можете просматривать задачи других пользователей");
        }

        List<Task> tasks = taskRepository.findByUserId(userId);
        List<TaskDtoResponse> dto = new ArrayList<>();
        tasks.forEach(t -> dto.add(taskMapper.toDto(t)));
        return dto;
    }

    public TaskDtoResponse saveTaskWithOwner(TaskDtoRequest dto, String currentUserEmail) {
        User current = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + currentUserEmail));

        if (isAdmin()) {
            // admin должен указать userId в dto
            if (dto.getUserId() == null || dto.getUserId() < 1) {
                throw new InvalidInputException("Admin должен указать userId");
            }
            User owner = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new NotFoundException(String.format(USER_WITH_ID_NOT_FOUND, dto.getUserId())));
            Task task = taskMapper.toEntity(dto);
            task.setUser(owner);
            taskRepository.save(task);
            return taskMapper.toDto(task);
        } else {
            // обычный пользователь — привязываем к себе
            Task task = taskMapper.toEntity(dto);
            task.setUser(current);
            taskRepository.save(task);
            return taskMapper.toDto(task);
        }
    }

    public TaskDtoResponse updateTaskWithAccess(long id, TaskDtoRequest dto, String currentUserEmail) {
        // просто делегируем к updateTask (уже делает проверки)
        return updateTask(id, dto);
    }

    public void deleteTaskWithAccess(long id, String currentUserEmail) {
        deleteTaskById(id);
    }

    public TaskDtoResponse findTaskByIdWithAccess(long id, String currentUserEmail) {
        return findTaskById(id);
    }
}
