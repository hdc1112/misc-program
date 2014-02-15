%define version 1.0.0

Summary:	ssh-keyinstall helps an ssh user set up the keys at both ends of an ssh connection.
Name:		ssh-keyinstall
Version:	%{version}
Release:	0
Copyright:	GPL
Group:		Applications/Internet
Source:		http://www.stearns.org/ssh-keyinstall/ssh-keyinstall-%{version}.tar.gz
URL:		http://www.stearns.org/ssh-keyinstall/
Vendor:		William Stearns
Packager:	William Stearns <wstearns@pobox.com>
BuildRoot:	/var/tmp/ssh-keyinstall-root
Buildarch:	noarch
Prereq:		/usr/bin/nc /usr/bin/head /bin/mkdir /bin/chmod /bin/cat /bin/grep



%description
ssh-keyinstall is a script that helps an ssh user set up the
keys at both ends of an ssh connection.  It creates an rsa or dsa key if
needed and copies the public half to the server.  Once the process is
done, you'll be able to log in with the passphrase and key instead of a
password.


%prep
%setup


#%build


%install
if [ "$RPM_BUILD_ROOT" = "/var/tmp/ssh-keyinstall-root" ]; then
	rm -rf $RPM_BUILD_ROOT
	mkdir -p $RPM_BUILD_ROOT/usr/bin
	mkdir -p $RPM_BUILD_ROOT/usr/share/doc/ssh-keyinstall-%{version}
	make DESTDIR=$RPM_BUILD_ROOT install
else
	echo Invalid Buildroot
	exit 1
fi
	

%clean
if [ "$RPM_BUILD_ROOT" = "/var/tmp/ssh-keyinstall-root" ]; then
	rm -rf $RPM_BUILD_ROOT
else
	echo Invalid Buildroot
	exit 1
fi


%files
%doc COPYING CREDITS ChangeLog INSTALL Makefile NEWS README TODO
%attr(755,root,root)			/usr/bin/ssh-keyinstall


