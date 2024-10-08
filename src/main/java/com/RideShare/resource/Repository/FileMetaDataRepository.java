package com.RideShare.resource.Repository;

import com.RideShare.resource.Enum.ResourceType;
import com.RideShare.resource.Model.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Integer> {

    public Optional<List<FileMetaData>> findAllByRelationTypeAndRelationId(ResourceType resourceType, Integer relationId);

    public Optional<FileMetaData> findByFileName(String name);


}
