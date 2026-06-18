package model;

public class Condition {
    private Integer id;
    private String condition;

    // Constructeurs
    public Condition() {}

    public Condition(Integer id, String condition) {
        this.id = id;
        this.condition = condition;
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}