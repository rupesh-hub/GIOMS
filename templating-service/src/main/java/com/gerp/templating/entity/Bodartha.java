package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bodartha {

	private String office;

	private String section;

	private String address;

	private String remarks;

	private boolean isExternal = false;

}
