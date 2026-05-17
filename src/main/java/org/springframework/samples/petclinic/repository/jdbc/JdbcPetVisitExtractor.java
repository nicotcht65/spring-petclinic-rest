/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.samples.petclinic.model.Visit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ResultSetExtractor} that maps a one-to-many relationship between
 * {@link JdbcPet} (root) and {@link Visit} (child) from a single joined query.
 */
public class JdbcPetVisitExtractor implements ResultSetExtractor<List<JdbcPet>> {

    private final JdbcPetRowMapper petRowMapper = new JdbcPetRowMapper();
    private final JdbcVisitRowMapper visitRowMapper = new JdbcVisitRowMapper();

    @Override
    public List<JdbcPet> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, JdbcPet> petMap = new LinkedHashMap<>();
        int rowNum = 0;
        while (rs.next()) {
            Integer petId = rs.getInt("pets_id");
            JdbcPet pet = petMap.get(petId);
            if (pet == null) {
                pet = petRowMapper.mapRow(rs, rowNum);
                petMap.put(petId, pet);
            }
            Integer visitPetId = rs.getObject("visits_pet_id") != null ? rs.getInt("visits_pet_id") : null;
            if (visitPetId != null) {
                Visit visit = visitRowMapper.mapRow(rs, rowNum);
                pet.addVisit(visit);
            }
            rowNum++;
        }
        return new ArrayList<>(petMap.values());
    }
}
