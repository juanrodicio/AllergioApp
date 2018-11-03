package es.uca.allergioapp.POJOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ingredient {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("relatedAllergies")
    @Expose
    private List<Object> relatedAllergies = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getRelatedAllergies() {
        return relatedAllergies;
    }

    public void setRelatedAllergies(List<Object> relatedAllergies) {
        this.relatedAllergies = relatedAllergies;
    }

}