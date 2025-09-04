package com.beyond.match.jwt.security;

// 나중에 또 다른 인증 방식 추가하면 그때 구현
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//    private final UserRepository userRepository;
//    @Override
//    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
//        User u = userRepository.findById(userId).orElseThrow(() ->
//                new  UsernameNotFoundException("User not found with userId: " + userId));
//        return new UserDetailsImpl(u);
//    }
//}