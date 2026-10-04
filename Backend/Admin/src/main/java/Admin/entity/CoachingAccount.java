package Admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "coaching", uniqueConstraints = {
        @UniqueConstraint(name = "uk_coaching_email", columnNames = "coaching_email"),
        @UniqueConstraint(name = "uk_coaching_name", columnNames = "coaching_name")
})
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoachingAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coaching_id")
    private Long coachingId;
    @Column(name = "coaching_name", nullable = false)
    private String coachingName;
    @Column(name = "coaching_address", nullable = false)
    private String coachingAddress;
    @Column(name = "coaching_email", nullable = false)
    private String coachingEmail;
    @Column(name = "coaching_owner_name", nullable = false)
    private String coachingOwnerName;
    @Column(nullable = false)
    private String password;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    private String role;
    private boolean verified;
}
