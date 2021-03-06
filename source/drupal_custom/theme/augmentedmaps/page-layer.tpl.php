<?php
// $Id: page.tpl.php,v 1.18 2008/01/24 09:42:53 goba Exp $
?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php print $language->language ?>" lang="<?php print $language->language ?>" dir="<?php print $language->dir ?>">
	<head>
		<title><?php print $head_title ?></title>
		<?php print $head ?>
		<?php print $styles ?>
		<?php print $scripts ?>
	</head>
	<body>


		<!-- Wrapper -->
		<div id="wrapper">
			<!-- Main menu -->
			<div id="menus">
				<div id="mainMenu">
					<?php if($main_menu): ?>
					<?php print $main_menu?>
					<?php endif; ?>
				</div>
				<div id="userMenu">
					<ul>
					<?php global $user; ?>
					<?php if($user->uid): ?>
					<li>Welcome <?php echo l($user->name, "user");?></li>
						<?php if($is_admin): ?>
						<li><a href="<?php print check_url($front_page);?>admin">Administration</a></li>
						<?php endif; ?>
					<li class="last"><a href="<?php print check_url($front_page);?>logout">Logout</a></li>
					<?php else: ?>
					<li><a href="#" id="login">Login</a></li>
					<li class="last"><a href="<?php print check_url($front_page);?>user/register">Register</a></li>
					<?php endif; ?>
					</ul>
				</div>
			</div>
			<!-- Main menu ends -->
			<!-- Guide -->
			<div id="guide">
				<!-- Branding -->
				<div id="branding">
					<!-- Logo -->
					<div id="logo">
						<a href="<?php print check_url($front_page)?>"><img src="<?php print check_url($front_page)?><?php print check_url(path_to_theme())?>/images/logo.jpg" alt="ecoPradesh"/></a>
					</div>
					<!-- Logo ends -->
				</div>
				<!-- Branding ends -->
				
				<!-- Search pane -->
				<div id="searchPane">
					<div id="mapSearch" class="paneBox">
					<?php if($map_search):?>
						<?php print $map_search;?>
					<?php endif; ?>
					</div>
				</div>
				<!-- Search pane ends -->
				
				
				
				<!-- Layers -->
				<div id="layerPane">
					<!--<h3>Layers</h3>-->
					
					
					<ul id="layerOptions">
						<li id="layerOption1"><a href="javascript:changeLayerOptions(1)">By Theme</a></li>
						<li id="layerOption2"><a href="javascript:changeLayerOptions(2)">By Geography</a></li>
					</ul>
					
					<div id="layers" class="paneBox">
						<?php if($nav): ?>
							<?php print $nav;?>
						<?php endif; ?>
						<div id="divThemes"></div>
						<div class="cl"></div>
					</div>
				</div>
				<!-- Layers end -->

				
				<!-- Footer -->
				<div id="footer">
					<?php if($footer): ?>
					<?php print $footer;?>
					<?php endif; ?>

					<div id="poweredBy">
						<a href="http://openlayers.org" target="_blank"><img src="<?php print check_url($front_page)?><?php print check_url(path_to_theme())?>/images/open-layers.jpg" alt="Open Layers"/></a> <a href="http://drupal.org" target="_blank"><img src="<?php print check_url($front_page)?><?php print check_url(path_to_theme())?>/images/drupal.jpg" alt="Powered by Drupal"/></a> <a href="http://opensource.org" target="_blank"><img src="<?php print check_url($front_page)?><?php print check_url(path_to_theme())?>/images/open-source.jpg" alt="Open Source"/></a> 
					</div>
				</div>
				<!-- Footer ends -->
			</div>
			<!-- Guide ends -->
			<!-- Main -->
			<div id="main">
				<!-- Content -->
				<div id="content">
					<div id="contentDiv">
						
						<div class="return">
							<a href="<?php print check_url($front_page);?>">&larr; Back to map</a>
						</div>
						
						<?php if ($tabs): print '<div id="tabs-wrapper" class="clear-block">'; endif; ?>
						<?php if ($title): print '<h2'. ($tabs ? ' class="with-tabs"' : '') .'>'. $title .'</h2>'; endif; ?>
						<?php if ($tabs): print '<ul class="tabs primary">'. $tabs .'</ul></div>'; endif; ?>
						<?php if ($tabs2): print '<ul class="tabs secondary">'. $tabs2 .'</ul>'; endif; ?>
						<?php if ($show_messages && $messages): print $messages; endif; ?>
						<?php print $help; ?>
						
						
						
						<?php print $content;?>
						
						<div class="return">
							<a href="<?php print check_url($front_page);?>">&larr; Back to map</a>
						</div>
					</div>

				</div>
				<!-- Content ends -->
				
				
				
			</div>
			<!-- Main ends -->
		</div>
		<!-- Wrapper end -->
		<?php print $closure;?>
    <script language="javascript">
      jQuery(document).ready(function(){
        setMainDivSize();
      });
    </script>
	</body>
</html>