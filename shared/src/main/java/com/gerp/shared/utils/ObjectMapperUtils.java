//package com.gerp.shared.utils;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.commons.beanutils.BeanUtils;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.convention.MatchingStrategies;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class ObjectMapperUtils<T , D> {
//
//    private static final ModelMapper modelMapper;
//    private static final ObjectMapper objectMapper;
//
//    /**
//     * Model mapper property setting are specified in the following block.
//     * Default property matching strategy is set to Strict see {@link MatchingStrategies}
//     * Custom mappings are added using {@link ModelMapper#addMappings(PropertyMap)}
//     */
//    static {
//        modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//        objectMapper = new ObjectMapper();
//
//    }
//
//    /**
//     * Hide from public usage.
//     */
//    private ObjectMapperUtils() {
//    }
//
//    /**
//     * <p>Note: outClass object must have default constructor with no arguments</p>
//     *
//     * @param <D>      type of result object.
//     * @param <T>      type of source object to map from.
//     * @param source   entity that needs to be mapped.
//     * @param outClass class of result object.
//     * @return new object of <code>outClass</code> type.
//     */
//    public  D mapValueToDestination(final T source) {
//        D outClass = modelMapper.map();
//         return outClass;
//    }
//
//    /**
//     * <p>Note: outClass object must have default constructor with no arguments</p>
//     *
//     * @param sourceList list of entities that needs to be mapped
//     * @param outCLass   class of result list element
//     * @param <D>        type of objects in result list
//     * @param <T>        type of entity in <code>entityList</code>
//     * @return list of mapped object with <code><D></code> type.
//     */
//    public  <D, T> List<D> mapAll(final List<T> sourceList, List<D> outCLass) {
//        return sourceList.stream()
//                .map(entity -> mapValue(entity))
//                .collect(Collectors.toList());
//    }
//    public static <D, T> List<D> mapAll(final Collection<T> sourceList, Class<D> outCLass) {
//        List<T> res = new ArrayList<>();
//        sourceList.stream().map(t -> )
//
//         sourceList.stream()
//                .map(entity -> map(entity, outCLass))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Maps {@code source} to {@code destination}.
//     *
//     * @param source      object to map from
//     * @param destination object to map to
//     */
//    public static <S, D> D map(final S source, D destination) {
//        modelMapper.map(source, destination);
//        return destination;
//    }
//}
