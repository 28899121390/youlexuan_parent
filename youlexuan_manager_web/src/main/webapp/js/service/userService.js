app.service("userService", function ($http) {
    this.showLoginName = function () {
        return $http.get("../user/showName.do");
    }
});