package com.vjsalon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.MediaAsset;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {}
