package com.ltj.user_center.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
    public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -6839700743351031965L;
        /**
        *  id
        */
        private Long teamId;
    }