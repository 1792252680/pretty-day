package com.xing.tools.apifox;

import java.util.List;
import lombok.Data;

@Data
public class Api{
	private String sourceUrl;
	private String path;
	private List<Object> cases;
	private String method;
	private int ordering;
	private String description;
	private String operationId;
	private String id;
	private String serverId;
	private String status;
	private List<String> tags;
	private List<MocksItem> mocks;
	private List<Object> parameters;
}