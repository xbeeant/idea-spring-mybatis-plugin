package org.mybatis.generator.myplugin;

import com.alibaba.fastjson.JSON;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.myplugin.antd.Column;
import org.xstudio.plugins.idea.ui.WebProperty;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntDesignPlugin extends PluginAdapter {

    private String key;

    private boolean viewModel;

    private boolean editModel;
    private boolean deleteAction;

    private String modelName;
    private String componentName;
    private int actionNumber = 0;
    private String targetProject;
    private Map<String, Map<String, Object>> webPropertyMap = new HashMap<>();

    @Override
    public boolean validate(List<String> warnings) {
        targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        targetProject = targetProject.replace("java", "web");
        List<WebProperty> webProperties = JSON.parseArray(properties.getProperty("web"), WebProperty.class);
        for (WebProperty webProperty : webProperties) {
            webPropertyMap.put(webProperty.getField(), webProperty.getProperties());
        }
        viewModel = true;
        editModel = true;
        deleteAction = true;
        modelName = JavaBeansUtil.getCamelCaseString(String.valueOf(webPropertyMap.get("namespace").get("namespace")), false);
        componentName = JavaBeansUtil.getCamelCaseString(modelName, true);
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

        generateComponent(introspectedTable, componentName);

        generateModel(modelName);

        generateViewModal(introspectedTable, componentName);
        generateEditModal(introspectedTable, componentName, modelName);
        generateService(introspectedTable, modelName);

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
                    "        width: " + actionNumber + " * actionWidth,\n" +
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
            setTableDefaultColumns(sb, introspectedColumn);
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            setTableDefaultColumns(sb, introspectedColumn);
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

    private void generateViewModal(IntrospectedTable introspectedTable, String componentName) {
        String filename = "view.js";
        StringBuilder sb = new StringBuilder();

        sb.append("import React, { PureComponent } from 'react';\n" +
                "import { Card, Modal } from 'antd';\n" +
                "import { DescriptionList } from 'caweb';\n" +
                "\n" +
                "const { Description } = DescriptionList;\n" +
                "\n" +
                "class " + componentName + "ViewModal extends PureComponent {\n" +
                "  render() {\n" +
                "    const { visible, onOk, onCancel, values, width, title } = this.props;\n" +
                "    return (\n" +
                "      <Modal\n" +
                "        width={width || 800}\n" +
                "        title={title || \"详情\"}\n" +
                "        visible={visible}\n" +
                "        onOk={onOk}\n" +
                "        onCancel={onCancel}\n" +
                "      >\n" +
                "        <Card bordered={false}>\n" +
                "          <DescriptionList size=\"small\" col={2}>\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            Map<String, Object> propertyObjectMap = webPropertyMap.get(introspectedColumn.getActualColumnName());
            sb.append("            <Description style={{ wordBreak: 'break-word' }} term=\"" + propertyObjectMap.get("remarks") + "\">{values." + introspectedColumn.getJavaProperty() + " || '-'}</Description>\n");
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            Map<String, Object> propertyObjectMap = webPropertyMap.get(introspectedColumn.getActualColumnName());
            sb.append("            <Description style={{ wordBreak: 'break-word' }} term=\"" + propertyObjectMap.get("remarks") + "\">{values." + introspectedColumn.getJavaProperty() + " || '-'}</Description>\n");
        }

        sb.append("          </DescriptionList>\n" +
                "        </Card>\n" +
                "      </Modal>\n" +
                "    );\n" +
                "  }\n" +
                "}\n");
        sb.append("export default " + componentName);
        sb.append("ViewModal;");
        try {
            writeFile("components/" + componentName, filename, sb.toString());
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    private void generateEditModal(IntrospectedTable introspectedTable, String componentName, String modelName) {
        String filename = "edit.js";
        StringBuilder sb = new StringBuilder();

        sb.append("import React, { PureComponent } from 'react';\n" +
                "import { connect } from 'dva';\n" +
                "import { Form, Row, Col, Input, Modal } from 'antd';\n" +
                "import { formItemLayout } from '@/utils/styles';\n" +
                "import styles from '@/global.less';\n" +
                "\n" +
                "const FormItem = Form.Item;\n" +
                "\n" +
                "@connect(({ " + modelName + ", loading }) => ({\n" +
                "  " + modelName + ",\n" +
                "  loading: loading.effects['" + modelName + "/get'],\n" +
                "}))\n" +
                "@Form.create()\n" +
                "class " + componentName + "EditModal extends PureComponent {\n" +
                "  uniqueValid = (field, fieldValues, callback) => {\n" +
                "    const { values, dispatch } = this.props;\n" +
                "    if (fieldValues && fieldValues.length > 0) {\n" +
                "      const payload = { " + key + ": values." + key + " };\n" +
                "      payload[field] = fieldValues;\n" +
                "      dispatch({\n" +
                "        type: '" + modelName + "/validate',\n" +
                "        payload,\n" +
                "        callback: (response) => {\n" +
                "          if (!response.success) {\n" +
                "            callback(response.msg);\n" +
                "          } else {\n" +
                "            callback();\n" +
                "          }\n" +
                "        },\n" +
                "      });\n" +
                "    } else {\n" +
                "      callback();\n" +
                "    }\n" +
                "  };\n" +
                "\n" +
                "  handleCancel = () => {\n" +
                "    const { onCancel } = this.props;\n" +
                "    onCancel();\n" +
                "  };\n" +
                "\n" +
                "  handleOk = () => {\n" +
                "    const { onOk, form, dispatch } = this.props;\n" +
                "    form.validateFields((err, values) => {\n" +
                "      if (err) {\n" +
                "        return;\n" +
                "      }\n" +
                "      let type = '" + modelName + "/add';\n" +
                "      if (values." + key + ") {\n" +
                "        type = '" + modelName + "/update';\n" +
                "      }\n" +
                "      dispatch({\n" +
                "        type,\n" +
                "        payload: values,\n" +
                "        callback: (response) => {\n" +
                "          if (response.success) {\n" +
                "            onOk(response.data);\n" +
                "          }\n" +
                "        },\n" +
                "      });\n" +
                "    });\n" +
                "  };\n" +
                "\n" +
                "  render() {\n" +
                "    const { form: { getFieldDecorator }, visible, title, values, width } = this.props;\n" +
                "    const me = this;\n" +
                "    return (\n" +
                "      <Modal\n" +
                "        width={width || 800}\n" +
                "        maskClosable={false}\n" +
                "        title={title || '编辑'}\n" +
                "        visible={visible}\n" +
                "        onOk={this.handleOk}\n" +
                "        onCancel={this.handleCancel}\n" +
                "      >\n" +
                "        <Form>\n" +
                "          <Row gutter={8}>\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append("            <Col span={24} className={styles.hidden}>\n" +
                    "              <FormItem\n" +
                    "                {...formItemLayout}\n" +
                    "                label=\"" + introspectedColumn.getJavaProperty() + "\"\n" +
                    "              >{getFieldDecorator('" + introspectedColumn.getJavaProperty() + "', {\n" +
                    "                initialValue: values." + introspectedColumn.getJavaProperty() + ",\n" +
                    "              })(<Input placeholder=\"系统自动生成无需编辑\" />)}\n" +
                    "              </FormItem>\n" +
                    "            </Col>\n");
        }

        Map<String, Object> propertyObjectMap;

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            propertyObjectMap = webPropertyMap.get(introspectedColumn.getActualColumnName());
            Object editable = propertyObjectMap.get("editable");
            Object remarks = propertyObjectMap.get("remarks");
            Object uniqueValidate = propertyObjectMap.get("uniqueValidate");
            if (null != editable && (Boolean) editable) {
                continue;
            }
            sb.append("            <Col span={24}>\n" +
                    "              <FormItem\n" +
                    "                {...formItemLayout}\n" +
                    "                label=\"" + remarks + "\"\n" +
                    "              >{getFieldDecorator('" + introspectedColumn.getJavaProperty() + "', {\n" +
                    "                initialValue: values." + introspectedColumn.getJavaProperty() + ",\n" +
                    "                validateTrigger: 'onBlur',\n" +
                    "                rules: [{ required: true, message: '" + remarks + "不能为空!' }");
            if (null != uniqueValidate && (Boolean) uniqueValidate) {
                sb.append(", {\n" +
                        "                  validator(rule, fieldValues, callback) {\n" +
                        "                    me.uniqueValid('" + introspectedColumn.getJavaProperty() + "', fieldValues, callback);\n" +
                        "                  },\n");
            }
            sb.append("                }],\n" +
                    "              })(<Input placeholder=\"请输入" + remarks + "\" />)}\n" +
                    "              </FormItem>\n" +
                    "            </Col>\n");
        }

        sb.append("          </Row>\n" +
                "        </Form>\n" +
                "      </Modal>\n" +
                "    );\n" +
                "  }\n" +
                "}\n");

        sb.append("export default ");
        sb.append(componentName + "EditModal;");
        try {
            writeFile("components/" + componentName, filename, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTableDefaultColumns(StringBuilder sb, IntrospectedColumn introspectedColumn) {
        Map<String, Object> propertyObjectMap = webPropertyMap.get(introspectedColumn.getActualColumnName());
        Object showInTable = propertyObjectMap.get("showInTable");
        if (null != showInTable && (Boolean) showInTable) {
            Column column = new Column((String) propertyObjectMap.get("remarks"), introspectedColumn.getJavaProperty());
            sb.append("      ");
            Object tableWidth = propertyObjectMap.get("tableWidth");
            if (null != tableWidth) {
                column.setWidth((String) tableWidth);
            }
            Object tableSorter = propertyObjectMap.get("tableSorter");
            if (null != tableSorter && (Boolean) tableSorter) {
                column.setSorter(true);
            }
            sb.append(JSON.toJSONString(column).replaceAll(",", ", ").replaceAll(":", ": "));
            sb.append(",");
            OutputUtilities.newLine(sb);
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
            writeFile("components/" + componentName + "/models", filename, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void generateService(IntrospectedTable introspectedTable, String modelName) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        String key = "";
        if (!primaryKeyColumns.isEmpty()) {
            key = primaryKeyColumns.get(0).getJavaProperty();
        }
        String filename = modelName + ".js";
        StringBuilder sb = new StringBuilder("import { stringify } from 'qs';\n" +
                "import request from '@/utils/request';\n" +
                "import { apiPrefix } from '@/config/config';\n" +
                "\n" +
                "/**\n" +
                " * 查询所有\n" +
                " * @param params\n" +
                " * @returns {Promise<*>}\n" +
                " */\n" +
                "export async function query(params) {\n" +
                "  return request(`${apiPrefix}/" + modelName + "/table?${stringify(params)}`);\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 获取详情\n" +
                " * @param params\n" +
                " * @returns {Promise<*>}\n" +
                " */\n" +
                "export async function get(params) {\n" +
                "  return request(`${apiPrefix}/" + modelName + "/?${stringify(params)}`);\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 移除\n" +
                " * @param params\n" +
                " * @returns {Promise<*>}\n" +
                " */\n" +
                "export async function remove(params) {\n" +
                "  return request(`${apiPrefix}/" + modelName + "/delete`, {\n" +
                "    method: 'POST',\n" +
                "    body: {\n" +
                "      ...params,\n" +
                "    },\n" +
                "  });\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 添加\n" +
                " * @param params\n" +
                " * @returns {Promise<*>}\n" +
                " */\n" +
                "export async function add(params) {\n" +
                "  return request(`${apiPrefix}/" + modelName + "`, {\n" +
                "    method: 'POST',\n" +
                "    body: {\n" +
                "      ...params,\n" +
                "      method: 'post',\n" +
                "    },\n" +
                "  });\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 更新\n" +
                " * @param params\n" +
                " * @returns {Promise<*>}\n" +
                " */\n" +
                "export async function update(params) {\n" +
                "  return request(`${apiPrefix}/" + modelName + "/${params." + key + "}`, {\n" +
                "    method: 'PUT',\n" +
                "    body: {\n" +
                "      ...params,\n" +
                "      method: 'post',\n" +
                "    },\n" +
                "  });\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 校验\n" +
                " * @param params\n" +
                " * @returns {Promise<*>}\n" +
                " */\n" +
                "export async function validate(params) {\n" +
                "  return request(`${apiPrefix}/" + modelName + "/validate`, {\n" +
                "    method: 'POST',\n" +
                "    body: {\n" +
                "      ...params,\n" +
                "      method: 'post',\n" +
                "    },\n" +
                "  });\n" +
                "}\n");
        try {
            writeFile("services", filename, sb.toString());
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

    @Override
    public boolean clientBasicCountMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientBasicDeleteMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientBasicInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientBasicSelectManyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientBasicSelectOneMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientBasicUpdateMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapBlobColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerApplyWhereMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }
}
