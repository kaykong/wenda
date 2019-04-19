(function (window, undefined) {
    var Action = Base.getClass('main.util.Action');
    var Business = Base.getClass('main.util.Business');

    Base.ready({
        initialize: fInitialize,
        // 事件代理
        events: {
            'click .js-like': fVote,
            'click .js-dislike': fVote
        }
    });

    function fInitialize() {
        var that = this;
        // 点击关注问题
        Business.followQuestion({
            countEl: $('.js-user-count'),
            listEl: $('.js-user-list')
        });
    }

    function getUrlRelativePath(url) {
        var arrUrl = url.split("//");

        var start = arrUrl[1].indexOf("/");
        var relUrl = arrUrl[1].substring(start);

        return relUrl;
    }

    function fVote(oEvent) {
        var that = this;
        var oEl = $(oEvent.currentTarget);
        var oDv = oEl.closest('div.js-vote');
        var sId = $.trim(oDv.attr('data-id'));
        var bLike = oEl.hasClass('js-like');
        if (!sId) {
            return;
        }
        if (that.isVote) {
            return;
        }
        that.isVote = true;
        Action[bLike ? 'like' : 'dislike']({
            commentId: sId,
            call: function (oResult) {
                // 调整样式
                oDv.find('.pressed').removeClass('pressed');
                //oDv.find(bLike ? '.js-like' : '.js-dislike').addClass('pressed');

                if (oResult.likeStatus > 0) {
                    oDv.find('.js-like').addClass('pressed');
                } else if (oResult.likeStatus < 0) {
                    oDv.find('.js-dislike').addClass('pressed');
                }

                // 更新数量
                oDv.closest('div.js-comment').find('span.js-voteCount').html(oResult.likeCount);
            },
            error: function (oResult) {
                if (oResult.code === 999) {
                    alert('请登录后再操作');
                    window.location.href = '/reglogin?next=' + window.decodeURIComponent(getUrlRelativePath(window.location.href));
                } else {
                    alert('出现错误，请重试');
                }
            },
            always: function () {
                that.isVote = false;
            }
        });
    }

    function fUnlike(oEvent) {
        var that = this;
        var oEl = $(oEvent.currentTarget);

    }

})(window);