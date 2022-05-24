package com.xing.tools.apifox;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class PermissionConverter {
    // id, method, name, parentId, permExpression, url
    static String insertApiSqlTemplate = "INSERT INTO permission " +
            "(id, createBy, status, updateBy, method, name, permExpression, sort, type, url)" +
            " VALUES " +
            "({}, 'system', 1, 'system', {}, {}, {}, 0, 'API', {});";
    static List<String> existList = ListUtil.of("file:upload:post", "image:apply:delete", "image:apply:feature:delete", "image:apply:feature:put", "image:apply:get", "image:apply:post", "image:apply:put", "image:apply:ship:delete", "image:apply:ship:detail:get", "image:apply:ship:get", "image:apply:ship:post", "image:apply:ship:put", "image:dynamic:snap:batch:delete", "image:dynamic:snap:batch:put", "image:dynamic:snap:delete", "image:dynamic:snap:detail:get", "image:dynamic:snap:get", "image:dynamic:snap:put", "image:ship:rcg:import:post", "image:ship:rcg:post", "image:ship:search:post", "image:standard:batch:delete", "image:standard:delete", "image:standard:detail:get", "image:standard:get", "image:standard:post", "image:standard:put", "ship:control:get", "ship:control:manual:delete", "ship:control:manual:detail:get", "ship:control:manual:get", "ship:control:manual:post", "ship:control:manual:put", "ship:warn:delete", "ship:warn:export:get", "ship:warn:get", "snap:address:delete", "snap:address:get", "snap:address:post", "snap:address:put", "user:delete", "user:get", "user:logout:post", "user:org:delete", "user:org:get", "user:org:post", "user:org:put", "user:permission:get", "user:post", "user:profile:get", "user:profile:post", "user:put");

    public static void main(String[] args) throws IOException {

        Files.list(Paths.get("src/main/java/com/xing/tools/apifox"))
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(filePath -> {
                    try {
                        ApiFoxApi apiFoxApi = JSONObject.parseObject(Files.readAllLines(filePath).get(0), ApiFoxApi.class);
                        ApiCollectionItem apiCollectionItem = apiFoxApi.getApiCollection().get(0);
                        List<ItemsItem> items = apiCollectionItem.getItems();
                        iteratorPrint(items);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static String concatLeftAndRight(String column) {
        return "'" + column + "'";
    }

    private static void iteratorPrint(List<ItemsItem> items) {
        if (CollUtil.isEmpty(items)) return;
        for (ItemsItem item : items) {
            Api api = item.getApi();
            if (api != null) {
                if ("deprecated".equals(api.getStatus())) continue;
                String permExpression = api.getPath()
                        .substring(1)
                        .replaceAll("/", ":") + ":" + api.getMethod();
                if (existList.contains(permExpression)) continue;
                String insertSql = StrUtil.format(insertApiSqlTemplate,
                        api.getId(),
                        concatLeftAndRight(api.getMethod().toUpperCase(Locale.ROOT)),
                        concatLeftAndRight(item.getName()),
                        concatLeftAndRight(permExpression),
                        concatLeftAndRight(api.getPath())
                );

                System.out.println(insertSql);
            } else {
                iteratorPrint(item.getItems());
            }

        }
    }

}

