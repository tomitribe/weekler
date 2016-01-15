<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Weekler</title>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <%!
        private String context;

        public void jspInit() { // compute it once only
            context = System.getProperty("weekler.context.base", getServletConfig().getServletContext().getContextPath());
            if (!context.startsWith("/")) {
                context = "/" + context;
            }
            if (!context.endsWith("/")) {
                context = context + "/";
            }
        }
    %>
    <script>
        (function () {
            document.write("<base href='//" + document.location.host + "<%= context %>' />");
        }());
    </script>
    <base href="<%= context %>"/>

    <link href="theme/css/bootstrap.min.css" rel="stylesheet">
    <link href="theme/css/one-page-wonder.css" rel="stylesheet">
    <link href="theme/css/custom.css" rel="stylesheet">
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script src="js/angular2/bundles/angular2-polyfills.min.js"></script>
    <script src="js/systemjs/dist/system.js"></script>
    <script src="js/rxjs/bundles/Rx.min.js"></script>
    <script src="js/angular2/bundles/angular2.min.js"></script>
    <script src="js/angular2/bundles/router.min.js"></script>
    <script>
        System.config({
            defaultJSExtensions: true,
            bundles: { 'app/app.js': ['boot'] },
            paths: {
                '*': 'js/*'
            }
        });
        System.import('boot').catch(console.error.bind(console));
    </script>

</head>

<body>
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Weekler</a>
        </div>
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li>
                    <a href="<%= context %>people">People</a>
                </li>
                <li>
                    <a href="<%= context %>schedule">Schedule</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="space"></div>
    <weekler>Loading....</weekler>

    <hr>
    <footer>
        <div class="row">
            <div class="col-lg-12">
                <p>Copyright &copy; Weekler 2016</p>
            </div>
        </div>
    </footer>
</div>

<script src="theme/js/jquery.js"></script>
<script src="theme/js/bootstrap.min.js"></script>
</body>

</html>
