package com.gerp.dartachalani.config.converter;

import com.gerp.dartachalani.cache.IDCacheRedisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UUIDtoLongConverter implements Converter<String, Long> {

	@Autowired
	private IDCacheRedisRepo idCacheRedisRepo;

	@Override
	public Long convert(@NonNull String guid) {
		try {
			Long id = idCacheRedisRepo.findByGUID(guid);
			if(id == null)
			    return Long.valueOf(guid);
			else
			    return id;
		}catch (Exception e){
			return Long.valueOf(0);
		}
	}
}
