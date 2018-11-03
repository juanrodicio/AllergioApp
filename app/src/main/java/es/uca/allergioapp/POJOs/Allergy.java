
package es.uca.allergioapp.POJOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Allergy {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("relatedIngredients")
    @Expose
    private List<Ingredient> relatedIngredients = null;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getRelatedIngredients() {
        return relatedIngredients;
    }

    public void setRelatedIngredients(List<Ingredient> relatedIngredients) {
        this.relatedIngredients = relatedIngredients;
    }

}
