package com.gerp.shared.mapper;

import java.util.List;

public interface GenericMapper<P, D> {
    /*
     * Convert given POJO into DTO
     */
    D toDto(P source);

    /*
     * Convert given dto to POJO
     */
    P toSource(D dto);

    /*
     * Convert given POJO list to DTO
     */
    List<D> toDto(List<P> pojos);

    /*
     * Convert given list of DTO to POJO
     */
    List<P> toModel(List<D> dtos);

}
