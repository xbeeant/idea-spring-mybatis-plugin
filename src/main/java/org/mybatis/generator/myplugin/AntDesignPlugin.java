package org.mybatis.generator.myplugin;

import com.alibaba.fastjson.JSON;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.myplugin.antd.Column;

import java.io.*;
import java.util.List;

public class AntDesignPlugin extends PluginAdapter {

    private String key;

    private boolean viewModel;

    private boolean editModel;
    private boolean deleteAction;

    private String modelName;
    private String componentName;
    private int actionNumber = 0;
    private String targetProject;

    @Override
    public boolean validate(List<String> warnings) {
        targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        targetProject = targetProject.replace("java", "web");
        viewModel = "是".equalsIgnoreCase(properties.getProperty("componentView", "否"));
        editModel = "是".equalsIgnoreCase(properties.getProperty("componentEdit", "否"));
        deleteAction = "是".equalsIgnoreCase(properties.getProperty("componentDelete", "否"));
        modelName = properties.getProperty("modelName");
        componentName = properties.getProperty("componentName");
        if (viewModel) {
            actionNumber = actionNumber + 1;
        }
        if (editModel) {
            actionNumber = actionNumber + 1;
        }
        if (deleteAction) {
            actionNumber = actionNumber + 1;
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        key = "id";
        if (!introspectedTable.getPrimaryKeyColumns().isEmpty()) {
            key = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();
        }
        String name = JavaBeansUtil.getCamelCaseString(introspectedTable.getFullyQualifiedTable().getFullyQualifiedTableNameAtRuntime(), false);

        generateComponent(introspectedTable, componentName);

        generateModel(modelName);

        super.initialized(introspectedTable);
    }

    private void generateComponent(IntrospectedTable introspectedTable, String componentName) {
        String filename = "index.js";
        StringBuilder sb = new StringBuilder();
        sb.append("import React, { PureComponent, Fragment } from 'react';\n" +
                "import { connect } from 'dva';\n" +
                "import { Divider, Popconfirm } from 'antd';\n" +
                "import { BasicTable } from 'caweb';\n" +
                "import { portal } from '@/config/config';\n");
        if (viewModel) {
            sb.append("import " + componentName + "ViewModal from './view';\n");
        }
        if (editModel) {
            sb.append("import " + componentName + "EditModal from './view';\n");
        }
        sb.append("\n");
        sb.append("import { width } from '@/utils/styles';\n");
        sb.append("\n");
        sb.append("const { word, uuid, action: actionWidth } = width;\n");
        sb.append("\n");
        sb.append("\n");
        sb.append("@connect(({ " + modelName + ", loading }) => ({\n" +
                "  " + modelName + ",\n" +
                "  loading: loading.effects['" + modelName + "/fetch'],\n" +
                "}))\n");
        sb.append("class " + componentName + "Table extends PureComponent {\n" +
                "  state = {\n" +
                "    values: null,\n");
        if (viewModel) {
            sb.append("    viewModalVisible: false,\n");
        }

        if (editModel) {
            sb.append("    editModalVisible: false,\n");
        }
        sb.append("  };\n");
        sb.append("\n");
        if (viewModel) {
            sb.append("  onView = record => {\n" +
                    "    const { onView } = this.props;\n" +
                    "    if (onView) {\n" +
                    "      onView(record, this);\n" +
                    "    } else {\n" +
                    "      this.setState({\n" +
                    "        viewModalVisible: true,\n" +
                    "        values: record,\n" +
                    "      });\n" +
                    "    }\n" +
                    "  };\n");
            sb.append("\n");
        }

        if (editModel) {
            sb.append("  onEdit = record => {\n" +
                    "    const { onEdit } = this.props;\n" +
                    "    if (onEdit) {\n" +
                    "      onEdit(record, this);\n" +
                    "    } else {\n" +
                    "      this.setState({\n" +
                    "        editModalVisible: true,\n" +
                    "        values: record,\n" +
                    "      });\n" +
                    "    }\n" +
                    "  };\n");
            sb.append("\n");
        }

        if (deleteAction) {
            sb.append("  onDelete = record => {\n" +
                    "    const { onDelete, dispatch } = this.props;\n" +
                    "    dispatch({\n" +
                    "      type: '" + modelName + "/remove',\n" +
                    "      payload: {\n" +
                    "        id: record.id,\n" +
                    "      },\n" +
                    "      callback: response => {\n" +
                    "        if (response.success && onDelete) {\n" +
                    "          onDelete(record);\n" +
                    "        }\n" +
                    "      },\n" +
                    "    });\n" +
                    "  };\n");
        }
        if (editModel || viewModel) {
            sb.append("  hideModal = (key, refresh = false) => {\n" +
                    "    const { refreshTable } = this.props;\n" +
                    "    const state = {};\n" +
                    "    state[key] = false;\n" +
                    "    this.setState(state);\n" +
                    "    if (refreshTable && refresh) {\n" +
                    "      refreshTable();\n" +
                    "    }\n" +
                    "  };\n");
        }

        sb.append("  render() {\n" +
                "    const { data, loading, action, selectedRows, onChange } = this.props;\n");
        if (viewModel || editModel) {
            sb.append("    const { ");
        }
        if (viewModel) {
            sb.append("viewModalVisible, ");
        }
        if (editModel) {
            sb.append("editModalVisible, ");
        }
        if (viewModel || editModel) {
            sb.append("values } = this.state;\n");
        }
        sb.append("    const columns = [];\n" +
                "    if (action) {\n" +
                "      Object.assign(action, { key: 'action' });\n" +
                "      columns.push(action);\n" +
                "    } ");
        if (actionNumber > 0) {
            sb.append("else {\n" +
                    "      columns.push({\n" +
                    "        key: 'action',\n" +
                    "        title: '操作',\n" +
                    "        width: '" + actionNumber + "* actionWidth',\n" +
                    "        render: (text, record) => (\n" +
                    "          <Fragment>\n");
            if (viewModel) {
                sb.append("            <a onClick={() => this.onView(record)}>详情</a>\n" +
                        "            <Divider type=\"vertical\" />\n");
            }
            if (editModel) {
                sb.append("            <a onClick={() => this.onEdit(record)}>编辑</a>\n" +
                        "            <Divider type=\"vertical\" />\n");
            }
            if (deleteAction) {
                sb.append("            <Popconfirm title=\"确认删除吗?\" onConfirm={() => this.onDelete(record)}>\n" +
                        "              <a href=\"#\">删除</a>\n" +
                        "            </Popconfirm>\n");
            }
            sb.append("          </Fragment>\n" +
                    "        ),\n" +
                    "      });\n" +
                    "    }\n" +
                    "\n");
        }
        sb.append("\n");
        sb.append("    const defaultColumns = [\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            Column column = new Column(introspectedColumn.getRemarks(), introspectedColumn.getJavaProperty());
            sb.append("      ");
            sb.append(JSON.toJSONString(column).replaceAll(",", ", ").replaceAll(":",": "));
            sb.append(",");
            OutputUtilities.newLine(sb);
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            Column column = new Column(introspectedColumn.getRemarks(), introspectedColumn.getJavaProperty());
            sb.append("      ");
            sb.append(JSON.toJSONString(column).replaceAll(",", ", ").replaceAll(":",": "));
            sb.append(",");
            OutputUtilities.newLine(sb);
        }
        sb.append("    ];\n" +
                "    columns.push(...defaultColumns);\n" +
                "\n" +
                "    return (\n" +
                "      <Fragment>\n" +
                "        <BasicTable\n" +
                "          selectedRows={selectedRows}\n" +
                "          loading={loading}\n" +
                "          data={data}\n" +
                "          rowKey=\"id\"\n" +
                "          columns={columns}\n" +
                "          onSelectRow={this.handleSelectRows}\n" +
                "          onChange={onChange}\n" +
                "        />\n");
        if (viewModel) {
            sb.append("        {viewModalVisible && (\n" +
                    "          <" + componentName + "ViewModal\n" +
                    "            visible={viewModalVisible}\n" +
                    "            values={values}\n" +
                    "            onOk={() => this.hideModal('viewModalVisible', true)}\n" +
                    "            onCancel={() => this.hideModal('viewModalVisible', false)}\n" +
                    "          />\n" +
                    "        )}\n");
        }

        if (editModel) {
            sb.append("        {editModalVisible && (\n" +
                    "          <" + componentName + "EditModal\n" +
                    "            visible={editModalVisible}\n" +
                    "            values={values}\n" +
                    "            onOk={() => this.hideModal('editModalVisible', true)}\n" +
                    "            onCancel={() => this.hideModal('editModalVisible', false)}\n" +
                    "          />\n" +
                    "        )}\n");
        }
        sb.append("      </Fragment>\n" +
                "    );\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "export default " + componentName + "Table;\n");
        try {
            writeFile("components/" + componentName, filename, sb.toString());
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }


    private void generateModel(String name) {
        String filename = name + ".js";
        String get = "get";
        String add = "add";
        String remove = "remove";
        String update = "update";
        String query = "query";
        String validate = "validate";
        StringBuilder sb = new StringBuilder("import {\n" +
                "  add,\n" +
                "  get,\n" +
                "  remove,\n" +
                "  update,\n" +
                "  query,\n" +
                "  validate,\n" +
                "} from '@/services/" + name + "';\n" +
                "\n" +
                "export default {\n" +
                "  namespace: '" + name + "',\n" +
                "  state: {\n" +
                "    data: { list: [], pagination: {} }, \n" +
                "  },\n" +
                "  effects: {\n" +
                "    *fetch({ payload }, { call, put }) {\n" +
                "      const response = yield call(" + query + ", payload);\n" +
                "      if (response.success) {\n" +
                "        yield put({\n" +
                "          type: 'save',\n" +
                "          payload: { data: response.data },\n" +
                "        });\n" +
                "      } else {\n" +
                "        yield put({\n" +
                "          type: 'save',\n" +
                "          payload: { data: { list: [], pagination: {}} },\n" +
                "        });\n" +
                "      }\n" +
                "    },\n" +
                "    *add({ payload, callback }, { call }) {\n" +
                "      const response = yield call(" + add + ", payload);\n" +
                "      if (callback){\n" +
                "        callback(response);\n" +
                "      }\n" +
                "    },\n" +
                "    *get({ payload, callback }, { call }) {\n" +
                "      const response = yield call(" + get + ", payload);\n" +
                "      if (callback){\n" +
                "        callback(response);\n" +
                "      }\n" +
                "    },\n" +
                "    *update({ payload, callback }, { call }) {\n" +
                "      const response = yield call(" + update + ", payload);\n" +
                "      if (callback){\n" +
                "        callback(response);\n" +
                "      }\n" +
                "    },\n" +
                "    *validate({ payload, callback }, { call }) {\n" +
                "      const response = yield call(" + validate + ", payload);\n" +
                "      if (callback){\n" +
                "        callback(response);\n" +
                "      }\n" +
                "    },\n" +
                "    *remove({ payload, callback }, { call }) {\n" +
                "      const response = yield call(" + remove + ", payload);\n" +
                "      if (callback){\n" +
                "        callback(response);\n" +
                "      }\n" +
                "    },\n" +
                "  },\n" +
                "\n" +
                "  reducers: {\n" +
                "    save(state, action) {\n" +
                "      return {\n" +
                "        ...state,\n" +
                "        ...action.payload,\n" +
                "      };\n" +
                "    },\n" +
                "  },\n" +
                "};\n");
        try {
            writeFile("models", filename, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeFile(String folderName, String filename, String content) throws IOException {
        String path = targetProject + "/" + folderName + "/";
        File file = new File(path);
        file.mkdirs();

        file = new File(path + filename);

        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        osw = new OutputStreamWriter(fos, "UTF-8");

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }
}
