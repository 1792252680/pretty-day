package com.xing.tools.apifox;

import java.util.List;
import lombok.Data;

@Data
public class MocksItem{
	private String name;
	private List<Object> conditions;
}