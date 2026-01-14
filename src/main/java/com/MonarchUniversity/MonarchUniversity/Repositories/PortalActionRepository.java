package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.PortalAction;

public interface PortalActionRepository extends JpaRepository<PortalAction, Long> {
	Optional<PortalAction> findByActionName(String name);
    List<PortalAction> findAllByActionName(String name);

}
