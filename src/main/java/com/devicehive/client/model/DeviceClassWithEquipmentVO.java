/**
 * Device Hive REST API
 * No descripton provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 * <p>
 * OpenAPI spec version: 2.1.1-SNAPSHOT
 * <p>
 * <p>
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.devicehive.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * DeviceClassWithEquipmentVO
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-12-09T14:31:42.381+02:00")
public class DeviceClassWithEquipmentVO {
    @SerializedName("id")
    private Long id = null;

    @SerializedName("name")
    private String name = null;

    @SerializedName("isPermanent")
    private Boolean isPermanent = false;

    @SerializedName("offlineTimeout")
    private Integer offlineTimeout = null;

    @SerializedName("data")
    private JsonStringWrapper data = null;

    @SerializedName("entityVersion")
    private Long entityVersion = null;

    @SerializedName("equipment")
    private List<DeviceClassEquipmentVO> equipment = new ArrayList<DeviceClassEquipmentVO>();

    public DeviceClassWithEquipmentVO id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     **/
    @ApiModelProperty(example = "null", value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeviceClassWithEquipmentVO name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     * @return name
     **/
    @ApiModelProperty(example = "null", required = true, value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceClassWithEquipmentVO isPermanent(Boolean isPermanent) {
        this.isPermanent = isPermanent;
        return this;
    }

    /**
     * Get isPermanent
     * @return isPermanent
     **/
    @ApiModelProperty(example = "null", value = "")
    public Boolean getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(Boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public DeviceClassWithEquipmentVO offlineTimeout(Integer offlineTimeout) {
        this.offlineTimeout = offlineTimeout;
        return this;
    }

    /**
     * Get offlineTimeout
     * @return offlineTimeout
     **/
    @ApiModelProperty(example = "null", value = "")
    public Integer getOfflineTimeout() {
        return offlineTimeout;
    }

    public void setOfflineTimeout(Integer offlineTimeout) {
        this.offlineTimeout = offlineTimeout;
    }

    public DeviceClassWithEquipmentVO data(JsonStringWrapper data) {
        this.data = data;
        return this;
    }

    /**
     * Get data
     * @return data
     **/
    @ApiModelProperty(example = "null", value = "")
    public JsonStringWrapper getData() {
        return data;
    }

    public void setData(JsonStringWrapper data) {
        this.data = data;
    }

    public DeviceClassWithEquipmentVO entityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
        return this;
    }

    /**
     * Get entityVersion
     * @return entityVersion
     **/
    @ApiModelProperty(example = "null", value = "")
    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }

    public DeviceClassWithEquipmentVO equipment(List<DeviceClassEquipmentVO> equipment) {
        this.equipment = equipment;
        return this;
    }

    public DeviceClassWithEquipmentVO addEquipmentItem(DeviceClassEquipmentVO equipmentItem) {
        this.equipment.add(equipmentItem);
        return this;
    }

    /**
     * Get equipment
     * @return equipment
     **/
    @ApiModelProperty(example = "null", value = "")
    public List<DeviceClassEquipmentVO> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<DeviceClassEquipmentVO> equipment) {
        this.equipment = equipment;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeviceClassWithEquipmentVO {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    isPermanent: ").append(toIndentedString(isPermanent)).append("\n");
        sb.append("    offlineTimeout: ").append(toIndentedString(offlineTimeout)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    entityVersion: ").append(toIndentedString(entityVersion)).append("\n");
        sb.append("    equipment: ").append(toIndentedString(equipment)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

