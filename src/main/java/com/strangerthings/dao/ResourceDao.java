package com.strangerthings.dao;

import java.util.List;

import com.strangerthings.model.Resource;

public interface ResourceDao {

    boolean create(Resource resource);

    List<Resource> findAll();

    Resource findById(int resourceId);

    boolean update(Resource resource);

    boolean delete(int resourceId);
}