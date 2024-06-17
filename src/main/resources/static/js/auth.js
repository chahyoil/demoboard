document.addEventListener('DOMContentLoaded', (event) => {
    fetchUserData();

    if(isEmpty(localStorage.accessToken)) {
        console.log('access token is empty');
        updateUI(false);
    } else {
        updateUI(true);
    }
});

function isEmpty(str){

    if(typeof str == "undefined" || str == null || str == "")
        return true;
    else
        return false ;
}

function fetchUserData() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if (accessToken) {
        fetch('/api/auth/userdata', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'RefreshToken': `Bearer ${refreshToken}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    // 액세스 토큰이 만료되었거나 유효하지 않은 경우
                    throw new Error('Unauthorized');
                }
                return response.json();
            })
            .then(data => {
                if(data.userName) {
                    console.log(data);
                    document.querySelector('body').dataset.isAuthenticated = true;
                    localStorage.setItem('userName', data.userName);
                    document.querySelector('body').dataset.authorities = data.authorities;
                    updateUI(true);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                // 필요 시 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
                refreshAccessToken();
            });
    } else {
        // 인증되지 않은 상태 설정
        document.querySelector('body').dataset.isAuthenticated = false;

        updateUI(false);
    }
}

function refreshAccessToken() {
    const refreshToken = localStorage.getItem('refreshToken');

    fetch('/api/auth/refresh', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${refreshToken}`
        },
        body: JSON.stringify({ refreshToken })
    })
        .then(response => response.json())
        .then(data => {
            if (data.accessToken) {
                localStorage.setItem('accessToken', data.accessToken);
                // 새로운 액세스 토큰을 사용하여 다시 요청
                fetchUserData();
            } else {
                // 리프레시 토큰도 만료된 경우 로그아웃 처리
                handleLogout();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            handleLogout();
        });
}

function handleLogout() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    const userName = localStorage.getItem('userName');

    fetch('/api/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
            'RefreshToken': `Bearer ${refreshToken}`
        },
        body: JSON.stringify({
            username: userName
        })
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Logout failed');
            }
        })
        .then(data => {
            console.log('Logout successful:', data);
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('userName');
            updateUI(false);
            Swal.fire('로그아웃 되었습니다.', '', 'success');

            // 최근 본 게시물 초기화
            clearRecentView();
        })
        .catch(error => {
            console.error('Error during logout:', error);
            Swal.fire('로그아웃 중 오류가 발생했습니다.', '', 'error');
        });
}

function clearRecentView() {
    document.querySelectorAll(".recent-post").forEach(function(content) {
        content.innerHTML = ''; // 'recent-post' 클래스의 모든 'tr' 요소를 초기화합니다.
    });
}

function updateUI(isAuthenticated) {
    const loginSection = document.getElementById('login-section');
    const signupSection = document.getElementById('signup-section');
    const logoutSection = document.getElementById('logout-section');
    // const myRecentPosts = document.querySelectorAll('.recent-post');

    if (isAuthenticated) {
        loginSection.style.visibility = 'hidden';
        loginSection.style.opacity = '0';
        loginSection.style.pointerEvents = 'none';
        signupSection.style.visibility = 'hidden';
        signupSection.style.opacity = '0';
        signupSection.style.pointerEvents = 'none';
        logoutSection.style.visibility = 'visible';
        logoutSection.style.opacity = '1';
        logoutSection.style.pointerEvents = 'auto';

    } else {
        loginSection.style.visibility = 'visible';
        loginSection.style.opacity = '1';
        loginSection.style.pointerEvents = 'auto';
        signupSection.style.visibility = 'visible';
        signupSection.style.opacity = '1';
        signupSection.style.pointerEvents = 'auto';
        logoutSection.style.visibility = 'hidden';
        logoutSection.style.opacity = '0';
        logoutSection.style.pointerEvents = 'none';

    }

}