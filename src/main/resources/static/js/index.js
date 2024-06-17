let currentPage = 0; // 현재 페이지 번호를 추적
let totalPages = 0; // 총 페이지 수를 추적
let currentCategory = 'ALL'; // 기본 카테고리 설정

document.addEventListener("DOMContentLoaded", function () {
    console.log('DOMContentLoaded event fired!');

    loadPosts('ALL');
    fetchRecentPosts();
    fetchMostViewedPosts();

    adjustPostLayout();
});

window.addEventListener('resize', function () {
    adjustPostLayout();
});

function adjustPostLayout() {

    const postContainer = document.querySelector(".blog-posts");
    const childrenCount = postContainer.querySelectorAll(".item").length

    if(childrenCount >= 21) {
        postContainer.style["justify-content"] = "space-around"
    } else {
        postContainer.style["justify-content"] = "flex-start"

    }
}

function fetchRecentPosts() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    const userName = getUserName(); // 사용자 ID를 가져오는 로직 추가

    if (userName) {
        fetch(`/api/public/posts/recent?userName=${userName}`,
            {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'RefreshToken': `Bearer ${refreshToken}`
                }
            })
            .then(response => response.json())
            .then(data => {
                const recentPostRows = document.querySelectorAll('.recent-post');
                data.forEach((post, index) => {
                    const postItem = createPostRow(post);
                    recentPostRows[index].innerHTML = postItem.innerHTML;
                });
            })
            .catch(error => {
                console.error('Error fetching recent posts:', error);
            });
    }
}


function fetchMostViewedPosts() {
    fetch('/api/public/posts/most-viewed')
        .then(response => response.json())
        .then(data => {
            const mostViewedPostRows = document.querySelectorAll('.most-viewed-post');
            data.forEach((post, index) => {
                const postItem = createPostRow(post);
                mostViewedPostRows[index].innerHTML = postItem.innerHTML;
            });
        })
        .catch(error => {
            console.error('Error fetching most viewed posts:', error);
        });
}

function createPostRow(post) {
    const postRow = document.createElement('tr');

    const iconCell = document.createElement('td');
    iconCell.className = 'icon';
    // iconCell.onclick = () => openModal(`post/post/${post.id}`, true);
    iconCell.setAttribute('onclick', `openModal('post/post/${post.id}', true)`); // 여기서 setAttribute를 사용하여 onclick 속성 설정
    const icon = document.createElement('i');

    icon.className = `ri-${getIcon(post.categoryName)}`;
    iconCell.appendChild(icon);
    postRow.appendChild(iconCell);

    const nameCell = document.createElement('td');
    nameCell.className = 'name';
    nameCell.textContent = post.title;
    postRow.appendChild(nameCell);

    const extensionCell = document.createElement('td');
    extensionCell.className = `category category-${post.categoryName.toLowerCase()}`;
    extensionCell.textContent = `${post.categoryName} `;
    postRow.appendChild(extensionCell);

    const sizeCell = document.createElement('td');
    sizeCell.className = 'content';
    sizeCell.textContent = `${(post.content)}`;
    postRow.appendChild(sizeCell);

    const moreCell = document.createElement('td');
    moreCell.className = 'more';
    const moreIcon = document.createElement('i');
    moreIcon.className = 'ri-more-fill';
    moreCell.appendChild(moreIcon);
    postRow.appendChild(moreCell);

    return postRow;
}


function loadPosts(category, page = 0) {
    currentCategory = category; // 현재 카테고리를 전역 변수에 저장
    const apiUrl = `/api/public/posts/allPosts?category=${encodeURIComponent(category)}&page=${page}`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            totalPages = data.totalPages; // 총 페이지 수 설정
            currentPage = page; // 현재 페이지 번호 설정

            const posts = data.content; // 게시물 목록은 data.content에 있음
            const postContainer = document.querySelector('.blog-posts');
            const newPostElement = document.querySelector('.item.new-post');

            // 기존 게시물 지우기 (new post 요소 다음의 모든 형제 요소 삭제)
            while (newPostElement.nextSibling) {
                newPostElement.nextSibling.remove();
            }

            // 새로운 게시물 추가
            posts.forEach(post => {
                const postItem = document.createElement('div');
                postItem.className = `item category-${post.categoryName.toLowerCase()}`; // 카테고리별 클래스를 추가
                postItem.dataset.id = post.id; // 데이터 속성에 id 저장

                const iconWrapper = document.createElement('div');
                iconWrapper.className = 'icon-wrapper';
                const icon = document.createElement('i');
                icon.className = `ri-${getIcon(post.categoryName)}`;
                iconWrapper.appendChild(icon);
                postItem.appendChild(iconWrapper);

                const contentWrapper = document.createElement('div');
                contentWrapper.className = 'content-wrapper';
                const title = document.createElement('h5');
                title.textContent = post.title;
                contentWrapper.appendChild(title);

                const category = document.createElement('p');
                category.textContent = post.categoryName;
                contentWrapper.appendChild(category);

                postItem.appendChild(contentWrapper);
                postContainer.appendChild(postItem);
            });

            // 페이지네이션 버튼 활성화/비활성화 설정
            updatePaginationButtons();
        })
        .catch(error => {
            console.error('Error fetching posts:', error);
        });
}

function updatePaginationButtons() {
    const prevButton = document.querySelector('.ri-skip-left-line');
    const nextButton = document.querySelector('.ri-skip-right-line');

    // 이전 버튼 활성화/비활성화
    if (currentPage > 0) {
        prevButton.classList.remove('disabled');
        prevButton.onclick = () => loadPosts(currentCategory, currentPage - 1);
    } else {
        prevButton.classList.add('disabled');
        prevButton.onclick = null;
    }

    // 다음 버튼 활성화/비활성화
    if (currentPage < totalPages - 1) {
        nextButton.classList.remove('disabled');
        nextButton.onclick = () => loadPosts(currentCategory, currentPage + 1);
    } else {
        nextButton.classList.add('disabled');
        nextButton.onclick = null;
    }
}

function getUserName() {
    return localStorage.getItem('userName');
    // return document.querySelector('body').dataset.userName;
}

// 초기 로드 시 카테고리와 페이지 번호 설정

loadPosts(currentCategory);

// 카테고리별 아이콘 선택 함수
function getIcon(categoryName) {
    switch (categoryName) {
        case 'MUSIC': return 'music-2-fill';
        case 'BOOK': return 'book-fill';
        case 'GAME': return 'gamepad-fill';
        case 'MOVIE': return 'film-fill';
        case 'RECIPE': return 'restaurant-fill';
        default: return 'file-3-line';
    }
}

// 이벤트 위임 사용
document.querySelector('.blog-posts').addEventListener('click', function (e) {
    const postItem = e.target.closest('.item');
    if (postItem && postItem.dataset.id) {
        openModal(`/post/post/${postItem.dataset.id}`, true);
    }
});

document.addEventListener('DOMContentLoaded', () => {
    loadPosts('ALL');
});