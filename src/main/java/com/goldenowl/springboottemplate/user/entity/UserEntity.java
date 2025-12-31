package com.goldenowl.springboottemplate.user.entity;

import com.goldenowl.springboottemplate.app.entity.BaseEntity;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.auth.entity.SignUpEntity;
import com.goldenowl.springboottemplate.user.listener.UserCompleteListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.util.Set;

@Entity
@Table(name = "GO_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SoftDelete
@EntityListeners(UserCompleteListener.class)
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column
    private String oauthId;

    /**
     * TODO: Review and change the fetch strategy of {@code roles} from EAGER to LAZY
     * to avoid unnecessary data loading and potential performance overhead.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "GO_USER_ROLES",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    /**
     * TODO: Remove this association after fully merging the sign-up flow
     * into {@code UserEntity} and completing data migration.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sign_up_id")
    private SignUpEntity signUp;
}
