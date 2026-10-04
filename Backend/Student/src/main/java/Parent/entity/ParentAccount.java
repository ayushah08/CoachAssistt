package Parent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Immutable
@Subselect("select parent_id, student_id, coaching_name, parent_name from parents")
@Synchronize("parents")
public class ParentAccount {
    @Id
    private Long parentId;
    @Column(nullable = false)
    private Long studentId;
    private String coachingName;
    private String parentName;

    protected ParentAccount() {}
    public Long getParentId() { return parentId; }
    public Long getStudentId() { return studentId; }
    public String getCoachingName() { return coachingName; }
    public String getParentName() { return parentName; }
}
