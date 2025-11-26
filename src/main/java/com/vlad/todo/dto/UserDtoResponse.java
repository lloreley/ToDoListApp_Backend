//package com.vlad.todo.dto;
//
//import lombok.Data;
//
//@Data
//public class UserDtoResponse {
//    private long id;
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String phone;
//}
package com.vlad.todo.dto;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
}