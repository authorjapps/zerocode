package org.jsmart.zerocode.zerocodejavaexec.pojo;

import java.util.Objects;

public class DbResult {
    private Integer id;
    private String name;

    public DbResult(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbResult dbResult = (DbResult) o;
        return Objects.equals(id, dbResult.id) &&
                Objects.equals(name, dbResult.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "DbResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
