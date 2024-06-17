
function openModal(src, isWide = false) {

    const modal = document.getElementById('modal');
    const iframeContainer = document.getElementById('iframe-container');
    const modalContent = document.querySelector('.modal-content');

    fetch(src)
        .then(response => response.text())
        .then(html => {
            iframeContainer.innerHTML = html;

            // 동적으로 로드된 HTML의 스크립트 실행
            const scriptTags = iframeContainer.querySelectorAll("script");

            scriptTags.forEach(script => {
                const newScript = document.createElement("script");
                newScript.textContent = script.textContent;
                document.head.appendChild(newScript).parentNode.removeChild(newScript);
            });

            if (isWide) {
                modalContent.classList.add('wide');
            } else {
                modalContent.classList.remove('wide');
            }
            modal.style.display = 'flex';
        })
        .catch(error => console.error('Error loading modal content:', error));
}

closeModal = function () {
    document.getElementById('modal').style.display = 'none';
}

// 모달 외부를 클릭하면 모달 닫기
window.onclick = function (event) {
    const modal = document.getElementById('modal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

// 키보드의 Esc 버튼을 클릭하면 모달 닫기
window.onkeydown = function (event) {
    const modal = document.getElementById('modal');
    if (event.key === 'Escape' || event.key === 'Esc') { // 'Esc'는 오래된 브라우저 호환성용
        modal.style.display = 'none';
    }
}