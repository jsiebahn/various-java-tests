package com.github.jsiebahn.various.tests.http.options.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 16:17
 */
@Component
public class ObjectMerger {

    private ObjectMapper objectMapper;

    @Autowired
    public ObjectMerger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T merge(T original, Map<String, Object> updateData) {

        JsonNode mainNode = objectMapper.valueToTree(original);
        JsonNode updateNode = objectMapper.valueToTree(updateData);

        JsonNode mergedNode = merge(mainNode, updateNode);

        try {
            //noinspection unchecked
            return objectMapper.treeToValue(mergedNode, (Class<T>) original.getClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // see http://stackoverflow.com/a/32447591
    protected JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

        Iterator<String> fieldNames = updateNode.fieldNames();

        while (fieldNames.hasNext()) {
            String updatedFieldName = fieldNames.next();
            JsonNode valueToBeUpdated = mainNode.get(updatedFieldName);
            JsonNode updatedValue = updateNode.get(updatedFieldName);

            // If the node is an @ArrayNode
            if (valueToBeUpdated != null && updatedValue.isArray()) {
                // running a loop for all elements of the updated ArrayNode
                for (int i = 0; i < updatedValue.size(); i++) {
                    JsonNode updatedChildNode = updatedValue.get(i);
                    // Create a new Node in the node that should be updated, if there was no corresponding node in it
                    // Use-case - where the updateNode will have a new element in its Array
                    if (valueToBeUpdated.size() <= i) {
                        ((ArrayNode) valueToBeUpdated).add(updatedChildNode);
                    }
                    // getting reference for the node to be updated
                    JsonNode childNodeToBeUpdated = valueToBeUpdated.get(i);
                    merge(childNodeToBeUpdated, updatedChildNode);
                }
                // if the Node is an @ObjectNode
            } else if (valueToBeUpdated != null && valueToBeUpdated.isObject()) {
                merge(valueToBeUpdated, updatedValue);
            } else {
                if (mainNode instanceof ObjectNode) {
                    ((ObjectNode) mainNode).replace(updatedFieldName, updatedValue);
                }
            }
        }
        return mainNode;
    }

}
