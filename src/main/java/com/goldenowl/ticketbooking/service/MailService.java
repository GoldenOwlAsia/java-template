package com.goldenowl.ticketbooking.service;

import com.goldenowl.ticketbooking.dto.request.CompleteUserMailDTO;
import com.goldenowl.ticketbooking.dto.request.VerifyUserMailDTO;

public interface MailService {

    void sendVerifyUserMail(VerifyUserMailDTO verifyUserMailDTO);
    void sendCompleteUserMail(CompleteUserMailDTO completeUserMailDTO);
}
