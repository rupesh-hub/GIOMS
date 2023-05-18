package com.gerp.shared.generic.api.converters;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractConverter<D, E> implements EntityMapper<D, E> {

    @Override
    public E toEntity(D dto) {
        return null;
    }

    @Override
    public E toEntity(D dto, E entity) {
        return null;
    }

    @Override
    public D toDto(E entity) {
        return null;
    }

    @Override
    public List<E> toEntity(List<D> dtoList) {
        if (dtoList == null) {
            return null;
        }

        if (dtoList.isEmpty()) {
            return null;
        }

        return dtoList.parallelStream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<D> toDto(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
