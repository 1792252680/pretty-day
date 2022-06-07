package com.xing.tools.apifox;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xing.mysql.PermissionConn;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * 根据ApiFox导出文档生成权限表
 */
public class PermissionConverter {
    /**
     * 根据项目动态设置数据库名称
     */
    static List<String> existPermExpressionList =
            PermissionConn.getPermExpressionList("bianjian_api");

    // 插入会插入permission. id, method, name, parentId, permExpression, url
    static String insertApiSqlTemplate =
            "INSERT INTO permission " +
                    "(id, createBy, status, updateBy, method, name, permExpression, sort, type, url)" +
                    " VALUES " +
                    "({}, 'system', 1, 'system', {}, {}, {}, 0, 'API', {});";
    // 更新只更新 permission.name(名字)
    static String updateApiSqlTemplate =
            "UPDATE permission SET name={} WHERE permExpression = {};";


    static Boolean isInsert = false;

    public static void main(String[] args) throws IOException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();

        Files.list(Paths.get(file.getAbsolutePath()))
                .filter(path -> path.toString().endsWith(".apifox.json"))
                .forEach(filePath -> {
                    try {
                        // ApiFox导出"忽略权限标签"后的json文件
                        ApiFoxApi apiFoxApi = JSONObject.parseObject(Files.readAllLines(filePath).get(0), ApiFoxApi.class);
                        ApiCollectionItem apiCollectionItem = apiFoxApi.getApiCollection().get(0);
                        List<ItemsItem> items = apiCollectionItem.getItems();
                        iteratorPrint(items);
                        if (isInsert) {
                            System.out.println(
                                    "\n" +
                                            "insert into role_permission(permissionId, roleId)\n" +
                                            "select id, 1\n" +
                                            "from permission\n" +
                                            "where id not in ( select permissionId from role_permission )"
                            );
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
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
                if (existPermExpressionList.contains(permExpression)) {
                    String updateSql = StrUtil.format(updateApiSqlTemplate,
                            concatLeftAndRight(item.getName()),
                            concatLeftAndRight(permExpression));
                    System.out.println(updateSql);
                    continue;
                }
                String insertSql = StrUtil.format(insertApiSqlTemplate,
                        api.getId(),
                        concatLeftAndRight(api.getMethod().toUpperCase(Locale.ROOT)),
                        concatLeftAndRight(item.getName()),
                        concatLeftAndRight(permExpression),
                        concatLeftAndRight(api.getPath())
                );

                System.out.println(insertSql);
                isInsert = true;
            } else {
                iteratorPrint(item.getItems());
            }

        }
    }

}

