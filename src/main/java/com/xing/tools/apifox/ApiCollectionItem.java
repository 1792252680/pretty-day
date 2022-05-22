package com.xing.tools.apifox;

import java.util.List;
import lombok.Data;

@Data
public class ApiCollectionItem{
	private String name;
	private String description;
	private String serverId;
	private List<ItemsItem> items;
	private int parentId;
}