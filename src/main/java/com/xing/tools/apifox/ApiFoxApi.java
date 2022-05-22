package com.xing.tools.apifox;

import java.util.List;

import lombok.Data;

@Data
public class ApiFoxApi {
    private List<ApiCollectionItem> apiCollection;
}