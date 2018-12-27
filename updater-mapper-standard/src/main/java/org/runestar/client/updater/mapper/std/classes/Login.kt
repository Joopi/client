package org.runestar.client.updater.mapper.std.classes

import org.runestar.client.updater.mapper.IdentityMapper
import org.runestar.client.updater.mapper.annotations.DependsOn
import org.runestar.client.updater.mapper.extensions.predicateOf
import org.runestar.client.updater.mapper.tree.Class2

@DependsOn(Client.Login_username::class)
class Login : IdentityMapper.Class() {

    override val predicate = predicateOf<Class2> { field<Client.Login_username>().klass == it }
}