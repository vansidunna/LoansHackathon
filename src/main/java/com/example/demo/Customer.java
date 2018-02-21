package com.example.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.*;


@Cache
@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

    @Id
    @NonNull
    private String id;

    private String name;
    private String bsn;
    private Long salary;

}