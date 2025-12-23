package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReposirory extends JpaRepository<User,Integer> {

}
