/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.gateway.core.config.dsl.external.deployer;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.kernel.utils.Utils;
import org.wso2.msf4j.Microservice;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 1.0.0
 */
@Component(
        name = "org.wso2.carbon.gateway.core.config.dsl.external.deployer.ConfigDeployer",
        service = Microservice.class,
        immediate = true
)
@Path("/service")
public class ConfigDeployer implements Microservice {

    private static final Logger log = LoggerFactory.getLogger(ConfigDeployer.class);

    @GET
    @Path("/")
    public String get() {
        return "Hello from WSO2 MSF4J";
    }

    @Path("/deploy")
    @POST
    @Consumes("text/plain")
    public Response post(String configModel) {
        if (configModel != null) {
            PrintWriter writer = null;
            try {

                byte data[] = DatatypeConverter.parseBase64Binary(configModel.split("=")[1]);

                String configuration = new String(data, "UTF-8");

                writer = new PrintWriter(Utils.getCarbonHome().toString() +
                        "/deployment/integration-flows/default.iflow", "UTF-8");
                writer.println(configuration);

            } catch (IOException e) {
                log.error("Error while saving configuration", e);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
