package com.akari.tickets.beans;

import java.util.List;

/**
 * Created by Akari on 2017/2/17.
 */

public class QueryTrainsResponse {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private String secretStr;
        private QueryLeftNewDTO queryLeftNewDTO;

        public String getSecretStr() {
            return secretStr;
        }

        public void setSecretStr(String secretStr) {
            this.secretStr = secretStr;
        }

        public QueryLeftNewDTO getQueryLeftNewDTO() {
            return queryLeftNewDTO;
        }

        public void setQueryLeftNewDTO(QueryLeftNewDTO queryLeftNewDTO) {
            this.queryLeftNewDTO = queryLeftNewDTO;
        }
    }
}
