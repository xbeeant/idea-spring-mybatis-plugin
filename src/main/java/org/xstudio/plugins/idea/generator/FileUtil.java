package org.xstudio.plugins.idea.generator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/3/11
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static String configFile = "generator-config.json";

    private FileUtil() {
    }

    public static JSONObject getConfig(String configPath) {
        File file = new File(configPath + configFile);
        if (!file.exists()) {
            return new JSONObject();
        }
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("", e);
        }

        return JSON.parseObject(sb.toString());
    }

    public static JSONObject saveConfig(String configPath, Map<String, JTextField> inputs) {
        JSONObject jsonObject = new JSONObject();
        File file = new File(configPath + configFile);
        BufferedWriter bw;
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), false)) {
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    Messages.showMessageDialog("配置文件存储失败", "提醒", Messages.getInformationIcon());
                }
            }
            bw = new BufferedWriter(fw);
            bw.write("{");
            bw.write("\n");
            for (Map.Entry<String, JTextField> fieldEntry : inputs.entrySet()) {
                String key = fieldEntry.getKey();
                JTextField value = fieldEntry.getValue();
                String text = value.getText();
                text = text.replaceAll("\\\\","\\\\\\\\");
                bw.write("\"");
                bw.write(key);
                bw.write("\":\"");
                bw.write(text.trim());
                bw.write("\",");
                bw.write("\n");
                jsonObject.put(key, text.trim());
            }
            bw.write("}");

            bw.close();
        } catch (Exception e) {
            logger.error("", e);
        }
        return jsonObject;
    }
}
