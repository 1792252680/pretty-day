package com.xing.tools.apifox;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class PermissionConverter {
    public static void main(String[] args) throws IOException {
        // id, method, name, parentId, permExpression, url
        String insertApiSqlTemplate = "INSERT INTO permission " +
                "(id, createBy, status, updateBy, method, name, parentId, permExpression, sort, type, url)" +
                " VALUES " +
                "({}, 'system', 1, 'system', {}, {}, {}, {}, 0, 'API', {});";


        Files.list(Paths.get("src/main/java/com/xing/tools/apifox"))
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(filePath -> {
                    try {
                        ApiFoxApi apiFoxApi = JSONObject.parseObject(Files.readAllLines(filePath).get(0), ApiFoxApi.class);


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}

