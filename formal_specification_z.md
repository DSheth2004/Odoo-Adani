# Formal Specification (Z Notation)

This document contains a formal system model for the project in Z notation.

## 1. Basic Types

[USER_ID, TEAM_ID, EQUIP_ID, REQUEST_ID]

ROLE ::= admin | manager | technician | user
STATUS ::= open | assigned | closed | rejected

Equipment ::= equipment(eid: EQUIP_ID, name: TEXT, available: BOOL)
User ::= user(uid: USER_ID, name: TEXT, email: TEXT, role: ROLE)
Request ::= request(rid: REQUEST_ID, requestor: USER_ID, equipment: EQUIP_ID, status: STATUS, assignedTo: TEAM_ID ?)

## 2. Abstract System State

AppState
  users: USER_ID \pfun User
  teams: TEAM_ID \pfun (P USER_ID)
  equipment: EQUIP_ID \pfun Equipment
  requests: REQUEST_ID \pfun Request
  inv: \forall uid: dom users @ (users uid).role \in ROLE
       \land \forall eid: dom equipment @ (equipment eid).available \in BOOL
       \land dom requests \subseteq REQUEST_ID
       \land dom teams \subseteq TEAM_ID

## 3. Invariants

AppStateInv ==
  AppState
  inv
  \forall r: dom requests @
    (requests r).requestor \in dom users
    \land (requests r).equipment \in dom equipment
    \land (requests r).status \in STATUS
    \land (requests r).assignedTo = \emptyset \lor (requests r).assignedTo \in dom teams

## 4. Operations

### 4.1 User Management Module

CreateUser
  \Delta AppState
  u?: User
  uid?: USER_ID
  pre: uid? \notin dom users
  post: users' = users \cup { uid? \mapsto u? }
        teams' = teams
        equipment' = equipment
        requests' = requests

Login
  \Xi AppState
  uid?: USER_ID
  result!: BOOL
  post: result! = (uid? \in dom users)

### 4.2 Equipment Module

AddEquipment
  \Delta AppState
  eid?: EQUIP_ID
  e?: Equipment
  pre: eid? \notin dom equipment
  post: equipment' = equipment \cup { eid? \mapsto e? }
        users' = users
        teams' = teams
        requests' = requests

UpdateEquipment
  \Delta AppState
  eid?: EQUIP_ID
  e?: Equipment
  pre: eid? \in dom equipment
  post: equipment' = equipment \oplus { eid? \mapsto e? }
        users' = users
        teams' = teams
        requests' = requests

### 4.3 Maintenance Request Module

CreateRequest
  \Delta AppState
  rid?: REQUEST_ID
  req?: Request
  pre: rid? \notin dom requests
       req?.requestor \in dom users
       req?.equipment \in dom equipment
       (equipment req?.equipment).available = TRUE
  post: requests' = requests \cup { rid? \mapsto req? }
        equipment' = equipment \oplus { req?.equipment \mapsto (equipment req?.equipment) \oplus { available \mapsto FALSE }}
        users' = users
        teams' = teams

AssignRequest
  \Delta AppState
  rid?: REQUEST_ID
  team?: TEAM_ID
  pre: rid? \in dom requests
       team? \in dom teams
       (requests rid?).status = open
  post: requests' = requests \oplus { rid? \mapsto (requests rid?) \oplus { status \mapsto assigned, assignedTo \mapsto team? }}
        users' = users
        equipment' = equipment
        teams' = teams

CloseRequest
  \Delta AppState
  rid?: REQUEST_ID
  pre: rid? \in dom requests
       (requests rid?).status = assigned
  post: requests' = requests \oplus { rid? \mapsto (requests rid?) \oplus { status \mapsto closed }}
        users' = users
        equipment' = equipment
        teams' = teams

### 4.4 Team / Role Module

CreateTeam
  \Delta AppState
  tid?: TEAM_ID
  members?: P USER_ID
  pre: tid? \notin dom teams
       members? \subseteq dom users
  post: teams' = teams \cup { tid? \mapsto members? }
        users' = users
        equipment' = equipment
        requests' = requests

AddTeamMember
  \Delta AppState
  tid?: TEAM_ID
  uid?: USER_ID
  pre: tid? \in dom teams
       uid? \in dom users
  post: teams' = teams \oplus { tid? \mapsto teams tid? \cup { uid? }}
        users' = users
        equipment' = equipment
        requests' = requests

RemoveTeamMember
  \Delta AppState
  tid?: TEAM_ID
  uid?: USER_ID
  pre: tid? \in dom teams
       uid? \in teams tid?
  post: teams' = teams \oplus { tid? \mapsto teams tid? \setminus { uid? }}
        users' = users
        equipment' = equipment
        requests' = requests

## 5. Example test scenarios linked to formal operations

- T1: CreateUser with new UID -> state expands by one user.
- T2: AddEquipment with new EID -> equipment set expands by one.
- T3: CreateRequest with open equipment -> request created, equipment unavailable.
- T4: AssignRequest for open request -> status assigned, assignedTo set.
- T5: CloseRequest for assigned request -> status closed.
- T6: CreateRequest with non-existing equipment -> precondition fails (invariant reject).

## 6. Notes

- This specification is a conceptual, formal description; the same constraints map to code invariants and automated tests.
- You can extend it with `Error` schemas for precondition failure returns.
