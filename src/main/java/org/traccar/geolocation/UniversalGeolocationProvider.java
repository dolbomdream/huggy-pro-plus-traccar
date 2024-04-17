/*
 * Copyright 2016 - 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.geolocation;

import org.traccar.model.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;
import java.util.Collection;
import org.traccar.model.WifiAccessPoint;
import org.traccar.model.CellTower;

public class UniversalGeolocationProvider implements GeolocationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversalGeolocationProvider.class);

    private final Client client;
    private final String url;

    public UniversalGeolocationProvider(Client client, String url, String key) {
        this.client = client;
        this.url = url + "?key=" + key;
        LOGGER.info(url);
    }

    @Override
    public void getLocation(Network network, final LocationProviderCallback callback) {
        StringBuilder builder = new StringBuilder();
        builder.append("MCC: ").append(network.getHomeMobileCountryCode()).append(" ");
        builder.append("MNC: ").append(network.getHomeMobileNetworkCode()).append(" ");
        builder.append("ConsiderIp: ").append(network.getConsiderIp()).append(" ");

        Collection<CellTower> cellTowers = network.getCellTowers();

        builder.append("CellTower: [");
        if (cellTowers != null) {
            for (CellTower tower : cellTowers) {
                builder.append("{");
                builder.append("CellId: ").append(tower.getCellId()).append(" ");
                builder.append("LocationAreaCode: ").append(tower.getLocationAreaCode()).append(" ");
                builder.append("SignalStrength: ").append(tower.getSignalStrength()).append(" ");
                builder.append("}");
            }
        }
        builder.append("] ");

        Collection<WifiAccessPoint> wifiAccessPoints = network.getWifiAccessPoints();
        builder.append("WifiAccessPoint: [");
        if (wifiAccessPoints != null) {
            for (WifiAccessPoint wifi : wifiAccessPoints) {
                builder.append("{");
                builder.append("macAddress: ").append(wifi.getMacAddress()).append(" ");
                builder.append("signalStrength: ").append(wifi.getSignalStrength()).append(" ");
                builder.append("}");
            }
        }
        builder.append("] ");
        LOGGER.info(builder.toString());

        client.target(url).request().async().post(Entity.json(network), new InvocationCallback<JsonObject>() {
            @Override
            public void completed(JsonObject json) {
                if (json.containsKey("error")) {
                    callback.onFailure(new GeolocationException(json.getJsonObject("error").getString("message")));
                } else {
                    JsonObject location = json.getJsonObject("location");
                    StringBuilder builder = new StringBuilder();
                    builder.append("lat: ").append(location.getJsonNumber("lat").doubleValue()).append(", ");
                    builder.append("lng:[").append(location.getJsonNumber("lng").doubleValue()).append(", ");
                    builder.append("accuracy: ").append(json.getJsonNumber("accuracy").doubleValue()).append("");
                    LOGGER.info(builder.toString());
                    callback.onSuccess(
                            location.getJsonNumber("lat").doubleValue(),
                            location.getJsonNumber("lng").doubleValue(),
                            json.getJsonNumber("accuracy").doubleValue());
                }
            }

            @Override
            public void failed(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

}
