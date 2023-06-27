package com.example.demo.Repositories;

import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Howfar;
import com.example.demo.Enums.Permissions;
import com.example.demo.Model.Notification;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
    @Query(value = "SELECT n FROM Notification n WHERE n.receiver = :user " +
            "AND n.permit = :permit " +
            "AND n.howfar <> :seen AND n.howfar <> :done")
    List<Notification> findByUserAndPermissionsAndNotHowfars(@Param("user") User user,
                                                         @Param("permit")Permissions permit,
                                                         @Param("seen") Howfar seen,
                                                         @Param("done") Howfar done);

    @Query(value = "SELECT n FROM Notification n WHERE n.receiver = :user " +
            "AND n.permit = :permit " +
            "AND n.howfar <> :howfar ")
    List<Notification> findByUserAndPermissionsAndNotHowfar(@Param("user") User user,
                                                         @Param("permit")Permissions permit,
                                                         @Param("howfar") Howfar howfar);
}
