package Coach_Service.service;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.Login.LoginResponse;
import Coach_Service.repository.CoachingRespoitory;
import Coach_Service.dto.Register.CoachingRegisterRequest;
import Coach_Service.dto.Register.CoachingRegisterResponse;
import Coach_Service.entity.Coaching;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
public class CoachingService {

    private final CoachingRespoitory coachingRespoitory;

    @Transactional
    public CoachingRegisterResponse register(CoachingRegisterRequest coachingRequest) {


        Coaching coachingdemo  = coachingRespoitory.findByCoachingName(coachingRequest.getCoachingName());
        if (!(coachingdemo == null)) {


            return CoachingRegisterResponse.builder()
                    .Message("Coaching Already Exists with the following details :-> ").coachingName(coachingRequest.getCoachingName()).CoachingOwnerName(coachingRequest.getCoachingOwnerName()).build();

        }

        Coaching coaching = Coaching.builder().coachingName(coachingRequest.
                        getCoachingName())
                .coaching_Address(coachingRequest.getCoaching_Address())
                .coaching_Email(coachingRequest.getCoaching_Address())
                .coachingOwnerName(coachingRequest.getCoachingOwnerName())
                .password(coachingRequest.getPassword())
                .dateTime(LocalDateTime.now(ZoneId.of("UTC+5:30")))
                .build();
        coachingRespoitory.save(coaching);


        return CoachingRegisterResponse.builder()
                .Message("Coaching Registered Succesfully with Following Details").
                coachingName(coaching.getCoachingName())
                .coaching_Email(coaching.getCoaching_Email())
                .CoachingOwnerName(coaching.getCoachingOwnerName())
                .coachingId(coaching.getCoachingId())
                .build();


    }



    @Transactional
    public LoginResponse login(CoachingLoginRequest register){

        Coaching coaching = coachingRespoitory.findByCoachingName(register.getCoaching_Name());

        LoginResponse loginResponse = new LoginResponse();
        if(!(coaching == null)) {
              loginResponse.setMessage("Coaching not found with Owner Name -> " + coaching.getCoachingOwnerName());
              return loginResponse;
        }


        String token = "kjrevrcjniu k45ed,+895/78 6huv435trg cbyegh.";
        coachingRespoitory.save(coaching);
        loginResponse.setMessage("Login Succesfull");
        loginResponse.setToken(token);

        return loginResponse;
    }


}
