package com.tgpj.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.tgpj.entity.UserRole;

@Configuration
public class MongoConfig {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return new MongoCustomConversions(List.of(
				new UserRoleReadConverter(),
				new UserRoleWriteConverter()));
	}

	@ReadingConverter
	static class UserRoleReadConverter implements Converter<String, UserRole> {
		@Override
		public UserRole convert(String source) {
			return UserRole.normalize(source);
		}
	}

	@WritingConverter
	static class UserRoleWriteConverter implements Converter<UserRole, String> {
		@Override
		public String convert(UserRole source) {
			return source == null ? UserRole.USER.name() : source.name();
		}
	}
}
