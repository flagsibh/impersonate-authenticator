# impersonate-authenticator

A implementation of a custom inbound authenticator. Compatible with WSO2 Identity Server 5.10.0.

What happens here is... person A is allowed to impersonate B, if person A has a special role to impersonate others and if person B let others with the special role to impersonate him.

With the WSO2 Identity Server, we can achieve this by writing a custom authenticator. We can implement by coupling to a existing authenticator or as a decoupled authenticator.
However I have implemented this as a coupled authenticator with basic authenticator. Reason behind this was the person I want to impersonate is sent in a query parameter (`?impersonatee=userb`).

## Implementation

Since I am coupling impersonation authentication with basic authenticator, I have extended the custom authenticator class by BasicAuthenticator. For this custom authenticator, I have override only public `AuthenticatorFlowStatus process(HttpServletRequest request, HttpServletResponse response, AuthenticationContext context)` method.

1. The authentication/authorization request redirects the user to the IAM's login page
2. The person being impersonated is passed as a query parameter, like `?impersonatee=userb`
3. Then, the impersonator must enters his/her credentials and hit **Login**
4. The custom authenticator will check if it is a valid user and authenticates him/her
5. If it is a valid user, then it will extract its roles and check if it matched with the impersonator role. (By default impersonator has `Internal/impadmin` role)
6. If it is there, the it will check if impersonatee has the necessary role which allows others with `Internal/impadmin` role to impersonate him/her. (By default impersonatee has `Internal/impuser`)
7. If these are satisfied, the user is created and set as the subject of this authentication step.
8. The application exchange the code for the token, which will have the subject set as the impersonated user

## Testing

1. Make an authorization request by calling the `authorize` endpoint:
   ```
   curl -k -X POST -d "response_type=code&client_id=zdeSNFqBEQdvBXxffF1VWeUs0kka&redirect_uri=https://localhost:9443/local/login&scope=internal_login internal_humantask_view openid&impersonatee=flags" https://localhost:9443/oauth2/authorize
   ```
    > Parameters should be adjusted to meet the current application requirements.   

2. as