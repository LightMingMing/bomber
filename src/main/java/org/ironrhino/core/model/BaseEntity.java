package org.ironrhino.core.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.ironrhino.core.hibernate.type.JsonType;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableId;

import lombok.Getter;
import lombok.Setter;

@TypeDefs({ @TypeDef(name = "json", typeClass = JsonType.class) })
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends AbstractEntity<String> {

    private static final long serialVersionUID = 5290168777920037800L;

    @SearchableId
    @Id
    @GeneratedValue(generator = "stringId")
    @GenericGenerator(name = "stringId", strategy = "stringId")
    @Column(length = 22)
    protected String id;

}