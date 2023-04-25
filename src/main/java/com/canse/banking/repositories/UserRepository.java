package com.canse.banking.repositories;

import com.canse.banking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

//    List<User> findAllByFirstname(String firstname); // *-*
//    List<User> findAllByFirstnameContaining(String firstname); // --*
    //List<User> findAllByFirstnameContainingIgnoreCase(String firstname);
//    List<User> findAllByAccount_Iban(String iban); // **-
    //User findByFirstnameContainingIgnoreCaseaAndEmail(String firstname, String email);

    @Query("from User where firstname = :firstname") // *-*
    List<User> serchByFirstName(String firstname);

    @Query("from User where firstname = '%:firstname%'")
    List<User> searchByFirstnameContaining(String firstname); // **-

    @Query("from User u inner join Account a on u.id = a.user.id where a.iban = :iban") // --*
    List<User> searchByIban(String iban);

    @Query(value = "SELECT * FROM _User u inner join account a on u.id = a.id_user and a.iban = :iban", nativeQuery = true) // **-
    List<User> searchByIbanNative(String iban);

    Optional<User> findByEmail(String email);


}
