package com.maintsync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 120)
    private String name;

    @Column(name = "company", length = 120)
    private String company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_user_id", nullable = false)
    @JsonProperty("member_user")
    private User memberUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public Team() {}

    public Team(Long id, String name, String company, User memberUser, Set<User> members, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.memberUser = memberUser;
        this.members = members != null ? members : new HashSet<>();
        this.createdAt = createdAt;
    }

    public static TeamBuilder builder() {
        return new TeamBuilder();
    }

    public static class TeamBuilder {
        private Long id;
        private String name;
        private String company;
        private User memberUser;
        private Set<User> members = new HashSet<>();
        private LocalDateTime createdAt;

        public TeamBuilder id(Long id) { this.id = id; return this; }
        public TeamBuilder name(String name) { this.name = name; return this; }
        public TeamBuilder company(String company) { this.company = company; return this; }
        public TeamBuilder memberUser(User memberUser) { this.memberUser = memberUser; return this; }
        public TeamBuilder members(Set<User> members) { this.members = members; return this; }
        public TeamBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Team build() {
            return new Team(id, name, company, memberUser, members, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public User getMemberUser() { return memberUser; }
    public void setMemberUser(User memberUser) { this.memberUser = memberUser; }

    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
