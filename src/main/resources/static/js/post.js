// console.log("post.js loaded");
//
//
// document.addEventListener("DOMContentLoaded", function () {
//     const postId = document.querySelector('meta[name="post-id"]').content;
//     console.log('postId:', postId); // 확인용 로그
//     fetchPostData(postId);
//
//     const postImage = document.querySelector('.post-image');
//     const postImagePlaceholder = document.querySelector('.post-image-placeholder');
//     if (!postImage.src || postImage.src.includes("path/to/image.jpg")) {
//         postImage.style.display = 'none';
//         postImagePlaceholder.style.display = 'block';
//     } else {
//         postImage.style.display = 'block';
//         postImagePlaceholder.style.display = 'none';
//     }
// });
//
// function fetchPostData(postId) {
//     fetch(`/api/posts/${postId}`)
//         .then(response => response.json())
//         .then(data => {
//             document.querySelector('.post-createdAt').textContent = new Date(data.createdAt).toLocaleDateString();
//             document.querySelector('.post-updatedAt').textContent = new Date(data.updatedAt).toLocaleDateString();
//             document.querySelector('.post-title').textContent = data.title;
//             document.querySelector('.post-author').textContent = data.userId;
//             document.querySelector('.post-category').textContent = data.categoryName;
//             document.querySelector('.post-isOpen').textContent = data.isOpen ? '공개' : '비공개';
//             document.querySelector('.post-content-text').textContent = data.content;
//
//             const commentList = document.querySelector('.comment-list');
//             commentList.innerHTML = '';
//             data.comments.forEach(comment => {
//                 const commentItem = document.createElement('div');
//                 commentItem.classList.add('comment-item');
//                 commentItem.id = `comment-${comment.id}`;
//                 commentItem.innerHTML = `
//                         <div class="comment-dates">
//                             작성일: <span class="comment-createdAt">${new Date(comment.createdAt).toLocaleDateString()}</span> | 수정일: <span class="comment-updatedAt">${new Date(comment.updatedAt).toLocaleDateString()}</span>
//                         </div>
//                         <p class="comment-author">${comment.userId}</p>
//                         <p class="comment-content">${comment.content}</p>
//                         <div class="comment-actions">
//                             <button class="btn-edit" onclick="editComment(${comment.id})">수정</button>
//                             <button class="btn-delete" onclick="deleteComment(${comment.id})">삭제</button>
//                         </div>
//                     `;
//                 commentList.appendChild(commentItem);
//             });
//         })
//         .catch(error => {
//             console.error('Error fetching post data:', error);
//             Swal.fire('포스트를 불러오는 중 오류가 발생했습니다.', '', 'error');
//         });
// }
//
// function editPost() {
//     const postTitle = document.querySelector('.post-title');
//     const postImage = document.querySelector('.post-image');
//     const postImageInput = document.querySelector('.post-image-input');
//     const postContentText = document.querySelector('.post-content-text');
//     const btnEdit = document.querySelector('.btn-edit');
//     const btnDelete = document.querySelector('.btn-delete');
//
//     btnDelete.style.display = 'none';
//
//     postTitle.outerHTML = `<input type="text" class="post-title" value="${postTitle.innerText}">`;
//     postImageInput.classList.add('active');
//     postContentText.outerHTML = `<textarea class="post-content-text" rows="10">${postContentText.innerText}</textarea>`;
//
//     btnEdit.innerText = '수정 완료';
//     btnEdit.onclick = async function () {
//         const newPostTitle = document.querySelector('.post-title').value;
//         const newPostImage = document.querySelector('.post-image-input').files[0];
//         const newPostContentText = document.querySelector('.post-content-text').value;
//
//         document.querySelector('.post-title').outerHTML = `<h2 class="post-title">${newPostTitle}</h2>`;
//         if (newPostImage) {
//             const reader = new FileReader();
//             reader.onload = function (e) {
//                 document.querySelector('.post-image').src = e.target.result;
//                 document.querySelector('.post-image').style.display = 'block';
//                 document.querySelector('.post-image-placeholder').style.display = 'none';
//             }
//             reader.readAsDataURL(newPostImage);
//         }
//         document.querySelector('.post-content-text').outerHTML = `<p class="post-content-text">${newPostContentText}</p>`;
//
//         btnEdit.innerText = '수정';
//         btnEdit.onclick = editPost;
//
//         btnDelete.style.display = 'inline';
//         postImageInput.classList.remove('active');
//
//         await Swal.fire('포스트가 수정되었습니다.', '', 'success');
//     };
//
//     const btnCancel = document.createElement('button');
//     btnCancel.innerText = '취소';
//     btnCancel.classList.add('btn-cancel');
//     btnCancel.onclick = function () {
//         location.reload();
//     };
//     document.querySelector('.post-actions').appendChild(btnCancel);
// }
//
// function deletePost() {
//     Swal.fire({
//         title: '정말로 이 포스트를 삭제하시겠습니까?',
//         text: '다시 되돌릴 수 없습니다. 신중하세요.',
//         icon: 'warning',
//         showCancelButton: true,
//         confirmButtonColor: '#3085d6',
//         cancelButtonColor: '#d33',
//         confirmButtonText: '삭제',
//         cancelButtonText: '취소',
//     }).then(result => {
//         if (result.isConfirmed) {
//             Swal.fire('포스트가 삭제되었습니다.', '', 'success').then(() => {
//                 window.location.href = '/';
//             });
//         }
//     });
// }
//
// function editComment(commentId) {
//     const commentItem = document.getElementById(`comment-${commentId}`);
//     const commentContent = commentItem.querySelector('.comment-content');
//
//     Swal.fire({
//         title: '댓글을 수정하세요:',
//         input: 'textarea',
//         inputValue: commentContent.innerText,
//         showCancelButton: true,
//         confirmButtonText: '수정 완료',
//         cancelButtonText: '취소'
//     }).then(result => {
//         if (result.isConfirmed) {
//             commentContent.innerText = result.value;
//             Swal.fire('댓글이 수정되었습니다.', '', 'success');
//         }
//     });
// }
//
// function deleteComment(commentId) {
//     Swal.fire({
//         title: '정말로 이 댓글을 삭제하시겠습니까?',
//         text: '다시 되돌릴 수 없습니다. 신중하세요.',
//         icon: 'warning',
//         showCancelButton: true,
//         confirmButtonColor: '#3085d6',
//         cancelButtonColor: '#d33',
//         confirmButtonText: '삭제',
//         cancelButtonText: '취소',
//     }).then(result => {
//         if (result.isConfirmed) {
//             const commentItem = document.getElementById(`comment-${commentId}`);
//             commentItem.remove();
//             Swal.fire('댓글이 삭제되었습니다.', '', 'success');
//         }
//     });
// }