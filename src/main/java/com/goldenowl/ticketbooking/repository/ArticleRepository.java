package com.goldenowl.ticketbooking.repository;

import com.goldenowl.ticketbooking.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, String> {

}
